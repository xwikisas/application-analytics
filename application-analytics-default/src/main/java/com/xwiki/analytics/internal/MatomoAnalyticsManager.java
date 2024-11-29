/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.xwiki.analytics.internal;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.stability.Unstable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.xpn.xwiki.XWikiException;
import com.xwiki.analytics.Aggregator;
import com.xwiki.analytics.AnalyticsManager;
import com.xwiki.analytics.JsonNormaliser;
import com.xwiki.analytics.configuration.AnalyticsConfiguration;
import com.xwiki.analytics.internal.aggregators.AggregatorDataHandler;
import com.xwiki.analytics.internal.aggregators.AggregatorDispatcher;

/**
 * Handle Matomo request and response manipulation.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Named("Matomo")
@Singleton
@Unstable
public class MatomoAnalyticsManager implements AnalyticsManager
{
    private static final String FAIL_RETRIEVE = "Error occurred while retrieving Matomo statistic results.";

    @Inject
    private Logger logger;

    @Inject
    private AnalyticsConfiguration configuration;

    @Inject
    private HttpClientBuilderFactory httpClientBuilderFactory;

    @Inject
    private Provider<List<JsonNormaliser>> jsonNormalizerProvider;

    @Inject
    private AggregatorDispatcher aggregatorDispatcher;

    @Inject
    private AggregatorDataHandler aggregatorDataHandler;

    @Override
    public void aggregate(String hint)
    {

        Aggregator aggregator = aggregatorDispatcher.getAggregator(hint);

        if (aggregator != null) {
            aggregator.aggregateData();
        } else {
            throw new RuntimeException(String.format("No aggregator found for [%s]", hint));
        }
    }

    @Override
    public Pair<Integer, List<JsonNode>> handleData(String hint, String asc, String sortField,
        Map<String, String> filters, int pageSize, int pageCount) throws JsonProcessingException, XWikiException
    {
        return this.aggregatorDataHandler.handleData(hint, asc, sortField, filters, pageSize, pageCount);
    }

    /**
     * Request specific data from Matomo and return an enhanced response.
     *
     * @param jsonNormaliserHint hint for the component that will alter the returned response
     * @param parameters a list of key, value pairs that will represent the parameters for the request
     * @return the altered Matomo request response, as JSON format
     */
    @Override
    public JsonNode requestData(Map<String, String> parameters, Map<String, String> filters, String jsonNormaliserHint)
        throws IOException
    {
        if (parameters == null) {
            logger.warn("Parameters must not be null.");
            throw new RuntimeException(FAIL_RETRIEVE);
        }
        parameters.put("idSite", configuration.getIdSite());
        parameters.put("token_auth", configuration.getAuthenticationToken());
        JsonNormaliser jsonNormaliser = this.selectNormaliser(jsonNormaliserHint);
        getJsonNormaliser(jsonNormaliserHint);
        return jsonNormaliser.normaliseData(executeHttpRequest(parameters), filters);
    }

    /**
     * Execute the HTTP request and returns the response body as a string.
     *
     * @param parameters the HTTP request parameters
     * @return response body as string
     * @throws IOException if there's a problem executing the HTTP request
     */
    private String executeHttpRequest(Map<String, String> parameters) throws IOException
    {
        try (CloseableHttpClient client = httpClientBuilderFactory.create()) {
            HttpGet request = new HttpGet(buildURI(parameters));
            try (CloseableHttpResponse response = client.execute(request)) {
                return EntityUtils.toString(response.getEntity());
            }
        }
    }

    /**
     * Create URI for Matomo request.
     *
     * @param parameterList List of the url parameters
     * @return The final URI in string format
     */
    private URI buildURI(Map<String, String> parameterList)
    {
        UriBuilder uriBuilder = UriBuilder.fromUri(configuration.getRequestAddress()).path("index.php");

        for (Map.Entry<String, String> entry : parameterList.entrySet()) {
            uriBuilder.queryParam(entry.getKey(), entry.getValue());
        }
        return uriBuilder.build();
    }

    /**
     * Gets the JsonNormaliser based on the hint.
     *
     * @param jsonNormaliserHint the hint
     * @return the JsonNormaliser instance
     * @throws RuntimeException if JsonNormaliser is null
     */
    private JsonNormaliser getJsonNormaliser(String jsonNormaliserHint)
    {
        JsonNormaliser jsonNormaliser = this.selectNormaliser(jsonNormaliserHint);
        if (jsonNormaliser == null) {
            logger.warn("There is no JSON normalizer associated with the [{}] hint you provided.", jsonNormaliserHint);
            throw new RuntimeException(FAIL_RETRIEVE);
        }
        return jsonNormaliser;
    }

    private JsonNormaliser selectNormaliser(String hint)
    {
        for (JsonNormaliser jsonNormaliser : this.jsonNormalizerProvider.get()) {
            if (hint.equals(jsonNormaliser.getIdentifier())) {
                return jsonNormaliser;
            }
        }
        return null;
    }
}

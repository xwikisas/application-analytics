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
package com.xwiki.analytics.internal.aggregators;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xwiki.analytics.Aggregator;
import com.xwiki.analytics.configuration.AggregatorConfiguration;
import com.xwiki.analytics.configuration.AnalyticsConfiguration;
import com.xwiki.analytics.internal.HttpClientBuilderFactory;

/**
 * Provides common behavior for all the aggregators.
 *
 * @version $Id$
 * @since 1.1
 */
public abstract class AbstractAggregator implements Aggregator
{
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String SEPARATOR = ",";

    @Inject
    protected AggregatorSaver aggregatorSaver;

    @Inject
    protected AnalyticsConfiguration matomoServerConfig;

    @Inject
    private HttpClientBuilderFactory httpClientBuilderFactory;

    @Inject
    private Logger logger;

    /**
     * Will compute the date from the global settings of the app in the yyyy-MM-dd format.
     *
     * @return time interval for witch the users expects the data.
     */
    protected String computeInterval(AggregatorConfiguration configuration)
    {
        // Formatter for yyyy-MM-dd format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (configuration.getTimeIntervalForStatistics().equals("custom")) {
            return configuration.getStartDate().substring(0, configuration.getStartDate().indexOf(" "))
                + SEPARATOR + configuration.getEndDate()
                .substring(0, configuration.getEndDate().indexOf(" "));
        } else if (configuration.getTimeIntervalForStatistics().equals("year")) {
            LocalDate now = LocalDate.now();
            // January 1st
            LocalDate startOfYear = now.withDayOfYear(1);
            // December 31st
            LocalDate endOfYear = now.withDayOfYear(now.lengthOfYear());
            return startOfYear.format(formatter) + SEPARATOR + endOfYear.format(formatter);
        } else if (configuration.getTimeIntervalForStatistics().equals("month")) {
            LocalDate now = LocalDate.now();
            // 1st day of the month
            LocalDate startOfMonth = now.withDayOfMonth(1);
            // Last day of the month
            LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
            return startOfMonth.format(formatter) + SEPARATOR + endOfMonth.format(formatter);
        } else if (configuration.getTimeIntervalForStatistics().equals("week")) {
            LocalDate now = LocalDate.now();
            // Start of the week
            LocalDate startOfWeek = now.with(ChronoField.DAY_OF_WEEK, 1);
            // End of the week
            LocalDate endOfWeek = startOfWeek.plusDays(6);
            return startOfWeek.format(formatter) + SEPARATOR + endOfWeek.format(formatter);
        } else {
            throw new IllegalArgumentException(
                "Unsupported interval type: " + configuration.getTimeIntervalForStatistics());
        }
    }

    /**
     * Execute the HTTP request and returns the response body as a string.
     *
     * @param uniformResourceIdentifier the request address.
     * @return response body as string if it was a success, an empty string otherwise.
     */
    protected String makeHttpRequest(URI uniformResourceIdentifier)
    {
        try (CloseableHttpClient client = httpClientBuilderFactory.create()) {
            HttpGet request = new HttpGet(uniformResourceIdentifier);
            CloseableHttpResponse response = client.execute(request);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            logger.error("Something went wrong when making a GET request [{}]", uniformResourceIdentifier, e);
            return "";
        }
    }

    /**
     * General method to build the basic URI needed for the request.
     *
     * @param baseURI base address of the server
     * @param parameters list of query parameters
     * @return basic uri the parameters
     */
    protected URI buildBaseURI(String baseURI, Map<String, String> parameters)
    {
        UriBuilder uriBuilder = UriBuilder.fromUri(baseURI).path("index.php");

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            uriBuilder.queryParam(entry.getKey(), entry.getValue());
        }
        return uriBuilder.build();
    }
}

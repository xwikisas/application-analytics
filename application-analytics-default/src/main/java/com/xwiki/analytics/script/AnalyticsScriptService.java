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
package com.xwiki.analytics.script;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.lang3.tuple.Pair;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.script.service.ScriptService;
import org.xwiki.stability.Unstable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.xpn.xwiki.XWikiException;
import com.xwiki.analytics.Aggregator;
import com.xwiki.analytics.AnalyticsManager;
import com.xwiki.analytics.configuration.AnalyticsConfiguration;
import com.xwiki.analytics.internal.aggregators.AggregatorDataHandler;

/**
 * Script service for the Analytics Application.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Named("analytics")
@Unstable
@Singleton
public class AnalyticsScriptService implements ScriptService
{
    @Inject
    private AnalyticsConfiguration configuration;

    @Inject
    @Named("Matomo")
    private AnalyticsManager analyticsManager;

    @Inject
    private AggregatorDataHandler aggregatorDataHandler;

    @Inject
    private Provider<List<Aggregator>> aggregators;

    /**
     * Get data from the analytics API, in normalized JSON format.
     *
     * @param parameters a map of the parameters needed for this request
     * @param filters holds the criteria for filtering a dataset.
     * @param jsonNormaliserHint hint specific to the component that will normalize the response, since it's given /
     *     resulted format depends on the context were is used.
     * @return a normalized JSON format
     */
    public JsonNode makeRequest(Map<String, String> parameters, Map<String, String> filters,
        String jsonNormaliserHint)
    {
        try {
            return analyticsManager.requestData(parameters, filters, jsonNormaliserHint);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed to get data for [%s]", jsonNormaliserHint), e);
        }
    }

    /**
     * Endpoint for all the aggregator jobs.
     *
     * @param hint for which aggregator you want to use
     */
    public void aggregate(String hint)
    {

        for (Aggregator aggregator : aggregators.get()) {
            if (hint.equals(aggregator.getHint())) {
                aggregator.aggregateData();
                return;
            }
        }
    }

    /**
     * Endpoint for handling aggregated data for livedata.
     * @param dataSource page where the data is stored
     * @param asc if you want the data to be in ascending or descending order
     * @param sortField the field that you want to sort after
     * @param filters map where the keys are the fields and the values are the text that you want to filter after
     * @param pageSize how many elements are on a page
     * @param pageCount current page offset
     * @return a list with jsons
     * @throws JsonProcessingException thrown if the dataSource doesn't store the data in JSON format
     * @throws XWikiException
     */
    public Pair<Integer, List<JsonNode>> handleData(DocumentReference dataSource, String asc, String sortField,
        Map<String, String> filters, int pageSize, int pageCount) throws JsonProcessingException, XWikiException
    {
        return this.aggregatorDataHandler.handleData(dataSource, asc, sortField, filters, pageSize, pageCount);
    }

    /**
     * @return the analytics configuration.
     */
    public AnalyticsConfiguration getConfiguration()
    {
        return configuration;
    }
}

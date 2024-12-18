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
package com.xwiki.analytics;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.xwiki.component.annotation.Role;
import org.xwiki.stability.Unstable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.xpn.xwiki.XWikiException;

/**
 * The interface for the AnalyticManger.
 *
 * @version $Id$
 * @since 1.0
 */
@Role
@Unstable
public interface AnalyticsManager
{
    /**
     * Request specific analytics data.
     *
     * @param jsonNormaliserHint hint to select the json normaliser
     * @param parameters a list of key, value pairs that will represent the parameters for the request
     * @param filters holds the criteria for filtering a dataset
     * @return a jsonNode with the processed data
     */
    JsonNode requestData(Map<String, String> parameters, Map<String, String> filters, String jsonNormaliserHint)
        throws IOException;

    /**
     * Aggregate data from multiple endpoints using a specific aggregator.
     *
     * @param hint which aggregator you want to use.
     */
    void aggregate(String hint);

    /**
     * Handles data aggregated from the analytics applications and stored directly in XWiki.
     *
     * @param hint for the aggregator used
     * @param asc if you want the data to be in ascending or descending order
     * @param sortField the field that you want to sort after
     * @param filters map where the keys are the fields and the values are the text that you want to filter after
     * @param pageSize how many elements are on a page
     * @param pageCount current page offset
     * @return a pair where the first element is the total number of entries that meet the criteria(filter), and the
     * second element is a slice of the original list paginated using pageSize and pageCount
     */
    Pair<Integer, List<JsonNode>> handleData(String hint, String asc, String sortField,
        Map<String, String> filters, int pageSize, int pageCount) throws JsonProcessingException, XWikiException;

}

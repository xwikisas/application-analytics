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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.script.service.ScriptService;
import org.xwiki.stability.Unstable;

import com.fasterxml.jackson.databind.JsonNode;
import com.xwiki.analytics.AnalyticsManager;

/**
 * Script service for the Analytics Application.
 *
 * @version $Id$
 * @since 1.1
 */
@Component
@Named("analytics")
@Unstable
@Singleton
public class AnalyticsScriptService implements ScriptService
{
    @Inject
    @Named("Matomo")
    private AnalyticsManager analyticsManager;

    /**
     * Get data from Matomo API, in a format specific to the macro that will use it.
     *
     * @param jsonNormaliserHint hint specific to the component that will normalize the Matomo result response,
     *     since it's given / resulted format depends on the context were is used.
     * @param filters holds the criteria for filtering a dataset.
     * @param parameters a map of the parameters needed for this request
     * @return response from Matomo API, in a normalized JSON format
     */
    public JsonNode getMatomoRequestResult(Map<String, String> parameters, Map<String, String> filters,
        String jsonNormaliserHint)
    {
        try {
            return analyticsManager.requestData(parameters, filters, jsonNormaliserHint);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed to get data for [%s]", jsonNormaliserHint), e);
        }
    }

    /**
     * Creates a subset from an array of jsons.
     *
     * @param jsonNode array of jsons
     * @param start index of the first element
     * @param end index+1 of the last element
     * @return a subset of jsons
     */
    public List<JsonNode> subSetJSON(JsonNode jsonNode, int start, int end)
    {
        List<JsonNode> subSet = new ArrayList<>();
        for (int i = start; i < end; i++) {
            subSet.add(jsonNode.get(i));
        }
        return subSet;
    }
}

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

import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.xwiki.analytics.JsonNormaliser;

/**
 * Abstract class for the JsonNormaliser that will serve as a bases for some of the normalisers.
 *
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractJsonNormaliser implements JsonNormaliser
{
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    protected static final String LABEL = "label";

    @Inject
    protected Logger logger;

    /**
     * Normalize Matomo response for format consistency and add extra information needed by XWiki.
     *
     * @param jsonString a string that has the expected format
     * @param filters holds the criteria for filtering a dataset
     * @return the normalised json
     * @throws JsonProcessingException
     */
    @Override
    public JsonNode normaliseData(String jsonString, Map<String, String> filters) throws JsonProcessingException
    {
        // Convert the string returned by Matomo in a JSON format to easily handle the processing of the nodes.
        JsonNode jsonRoot = OBJECT_MAPPER.readTree(jsonString);
        // Matomo may return several variants of JSON formats. In one scenario, when the period is set to
        // day/week/month/year, it returns a JSON object with keys representing dates. The corresponding value for
        // each key is an array of JSON objects, each of which represents a page. However, if the user sets the
        // period parameter to "range" Matomo returns an array of JSON objects, with each JSON object representing
        // a page. Due to these variations, the result needs to be processed to create a single format.
        // This normalized format is an array of JSON objects and each JSON object in this array will have a new
        // field called 'date'. This 'date' field will be set to N/A when Matomo returns an array instead of an
        // object. For both type of formats, the label field is also altered in order to contain the full page name
        if (jsonRoot.isArray()) {
            return processArrayNode(jsonRoot, filters);
        } else {
            return processObjectNode(jsonRoot, filters);
        }
    }

    /**
     * Handles each entry that was returned by Matomo and processes it.
     *
     * @param jsonNode an array of jsons
     * @param filters  holds the criteria for filtering a dataset
     * @return array of jsons
     */
    protected JsonNode processArrayNode(JsonNode jsonNode, Map<String, String> filters)
    {
        ArrayNode arrayNode = OBJECT_MAPPER.createArrayNode();
        for (JsonNode objNode : jsonNode) {
            if (objNode.isObject() && matchesAllFilters(objNode, filters)) {
                arrayNode.add(processNode(objNode, null));
            }
        }
        return arrayNode;
    }

    /**
     * Handle the scenario where Matomo returns an object with dates as keys and arrays of JSON objects as values. This
     * function extracts the date from the key and adds it to each page. Ultimately, it returns an array of JSON
     * objects.
     *
     * @param jsonNode the response from Matomo that is a json
     * @param filters  holds the criteria for filtering a dataset
     * @return array of jsons that have been processed
     */
    protected JsonNode processObjectNode(JsonNode jsonNode, Map<String, String> filters) throws JsonProcessingException
    {
        ArrayNode arrayNode = OBJECT_MAPPER.createArrayNode();
        Iterator<String> fieldNames = jsonNode.fieldNames();
        while (fieldNames.hasNext()) {
            String date = fieldNames.next();
            JsonNode childNode = jsonNode.get(date);
            for (JsonNode objNode : childNode) {
                if (objNode.isObject() && matchesAllFilters(objNode, filters)) {
                    arrayNode.add(processNode(objNode, null));
                }
            }
        }
        return arrayNode;
    }

    protected boolean matchesAllFilters(JsonNode objNode, Map<String, String> filters)
    {
        if (filters == null) {
            return true;
        }
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String filterField = entry.getKey();
            String filterValue = entry.getValue();
            if (!(objNode.has(filterField) && objNode.get(filterField).asText().contains(filterValue))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Process the current node and add it to the final array of jsons.
     *
     * @param currentNode the current json that has to be processed
     * @param extraValues map of values that can be used for processing  the node
     */
    protected abstract JsonNode processNode(JsonNode currentNode, Map<String, String> extraValues);
}

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.stability.Unstable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Normalizes the response needed by the RowEvolution feature.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Named("RowEvolution")
@Unstable
@Singleton
public class RowEvolutionJsonNormaliser extends AbstractJsonNormaliser
{
    /**
     * Hint for the RowEvolution.
     */
    public static final String HINT = "RowEvolution";

    private static final String DATE = "date";

    @Override
    public String getIdentifier()
    {
        return RowEvolutionJsonNormaliser.HINT;
    }

    /**
     * Transforming the json object returned by Matomo into an array of jsons to make it easier to use in javascript and
     * adding a date to each entry to be able to idenitfy for what period the statistics are.
     *
     * @param jsonNode the response from Matomo that is a json
     * @param filters holds the criteria for filtering a dataset
     * @return filtered array of processed jsons with the date added to them
     */
    protected ArrayNode processObjectNode(JsonNode jsonNode, Map<String, String> filters) throws JsonProcessingException
    {
        ArrayNode arrayNode = OBJECT_MAPPER.createArrayNode();
        Iterator<String> fieldNames = jsonNode.fieldNames();
        Map<String, String> processingValues = new HashMap<>();
        while (fieldNames.hasNext()) {
            String date = fieldNames.next();
            processingValues.put(DATE, date);
            JsonNode childNode = jsonNode.get(date);
            boolean nodeFound = true;
            for (JsonNode node : childNode) {
                if (matchesAllFilters(node, filters)) {
                    arrayNode.add(this.processNode(node, processingValues));
                    nodeFound = false;
                    break;
                }
            }
            // When the node is empty or the entry wasn't found then we create a new node with the current date and
            // add it to the array.
            if (childNode.get(0) == null || nodeFound) {
                arrayNode.add(processNode(OBJECT_MAPPER.createObjectNode(), processingValues));

            }
        }
        return arrayNode;
    }

    @Override
    protected JsonNode processNode(JsonNode currentNode, Map<String, String> processingValues)
    {
        ((ObjectNode) currentNode).put(DATE, processingValues.get(DATE));
        return currentNode;
    }

    @Override
    protected boolean matchesAllFilters(JsonNode objNode, Map<String, String> filters)
    {
        if (filters == null) {
            return true;
        }
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String filterField = entry.getKey();
            String filterValue = entry.getValue();
            // Exact match instead of partial match like in the original method.
            if (!(objNode.has(filterField) && objNode.get(filterField).asText().equals(filterValue))) {
                return false;
            }
        }
        return true;
    }
}

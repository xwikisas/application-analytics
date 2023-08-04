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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;

/**
 * @version $Id$
 */
public class MatomoJsonNormaliser implements JsonNormaliser
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String DATE = "date";

    /**
     * @param jsonString The json provided by Matomo.
     * @return The normalised json as a string.
     * @throws JsonProcessingException
     */
    public JsonNode normaliseData(String jsonString) throws JsonProcessingException
    {
        JsonNode jsonNode = OBJECT_MAPPER.readTree(jsonString);
        if (jsonNode.isArray()) {
            processArrayNode(jsonNode);
        } else {
            jsonNode = processObjectNode(jsonNode);
        }
        return jsonNode;
    }

    /**
     * This function will add the date field to all rows when the user uses period="range" and the data is merged.
     *
     * @param jsonNode
     */

    private void processArrayNode(JsonNode jsonNode)
    {
        for (JsonNode objNode : jsonNode) {
            if (objNode.isObject()) {
                ((ObjectNode) objNode).put(DATE, "");
            }
        }
    }

    /**
     * This function will process the json from Matomo and return a new one with the date field added.
     *
     * @param jsonNode
     * @return
     */
    private ArrayNode processObjectNode(JsonNode jsonNode)
    {
        ArrayNode arrayNode = OBJECT_MAPPER.createArrayNode();
        Iterator<String> fieldNames = jsonNode.fieldNames();
        while (fieldNames.hasNext()) {
            String date = fieldNames.next();
            JsonNode childNode = jsonNode.get(date);
            for (JsonNode objNode : childNode) {
                if (objNode.isObject()) {
                    ((ObjectNode) objNode).put(DATE, date);
                    arrayNode.add(objNode);
                }
            }
        }
        return arrayNode;
    }
}

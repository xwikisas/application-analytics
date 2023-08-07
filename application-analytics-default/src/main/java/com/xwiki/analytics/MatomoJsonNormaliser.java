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
     * This method will normalise the jsons returned by Matomo into a single format.
     * @param jsonString The json provided by Matomo.
     * @return The normalised json as a string.
     * @throws JsonProcessingException Throws this error when the jsonString param is not a proper json.
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
     * This function will add the date field to all rows when the user uses and the data is merged and is returned
     * as an array of jsons.
     * @param jsonNode The json node created from the Matomo response.
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
     * This function will process the data returned by Matomo when it returns a json and each field represents a time
     * interval.
     * @param jsonNode The json node created from the Matomo response.
     * @return Will return a new array of jsons.
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

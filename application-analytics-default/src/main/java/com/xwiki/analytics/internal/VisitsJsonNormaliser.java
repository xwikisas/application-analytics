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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.xwiki.component.annotation.Component;
import org.xwiki.stability.Unstable;

import javax.inject.Named;
import javax.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Normalizes the response required by macros, which consist of graphs.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Named(VisitsJsonNormaliser.HINT)
@Unstable
@Singleton
public class VisitsJsonNormaliser extends AbstractJsonNormaliser
{
    /**
     * Hint for the VisitsJsonNormaliser.
     */
    public static final String HINT = "VisitsSummary";

    private static final String LABEL = "labels";

    private static final String VALUES = "values";

    @Override
    public String getIdentifier()
    {
        return VisitsJsonNormaliser.HINT;
    }

    @Override
    protected JsonNode processObjectNode(JsonNode jsonNode, Map<String, String> filters) throws JsonProcessingException
    {
        ObjectNode result = OBJECT_MAPPER.createObjectNode();
        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        jsonNode.fieldNames().forEachRemaining(currentDate -> {
            labels.add(currentDate);
            JsonNode currentNode = jsonNode.get(currentDate);
            // If there are missing values for a day, Matomo returns an empty array.
            if (currentNode.isInt()) {
                values.add(currentNode.asInt());
            } else {
                values.add(currentNode.isTextual()
                    ? Integer.parseInt(currentNode.asText().replace("%", "")) : 0);
            }
        });
        result.set(LABEL, OBJECT_MAPPER.valueToTree(labels));
        result.set(VALUES, OBJECT_MAPPER.valueToTree(values));
        return result;
    }
}

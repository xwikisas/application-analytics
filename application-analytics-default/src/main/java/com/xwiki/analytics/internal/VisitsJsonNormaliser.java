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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;


/**
 * Normalizes the response needed by the RowEvolution feature.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Named("VisitsSummary")
@Unstable
@Singleton
public class VisitsJsonNormaliser extends AbstractJsonNormaliser
{

    /**
     * Hint for the VisitsJsonNormaliser.
     */
    public static final String HINT = "VisitsSummary";
    private static final String LABEL = "labels";
    @Override
    public String getIdentifier()
    {
        return VisitsJsonNormaliser.HINT;
    }


    @Override
    protected JsonNode processObjectNode(JsonNode jsonNode, Map<String, String> filters) throws JsonProcessingException
    {
        List<String> labels = new ArrayList<>();
        Map<String, List<Integer>> resultsMap = new HashMap<>();
        String [] fields = {"nb_visits", "avg_time_on_site", "bounce_count", "nb_actions_per_visit", "max_actions"};
        Arrays.stream(fields).forEach(field -> resultsMap.put(field, new ArrayList<>()));

        jsonNode.fieldNames().forEachRemaining(currentDate -> {
            JsonNode content = jsonNode.get(currentDate);
            labels.add(currentDate);
            Arrays.stream(fields).forEach(field ->
            {
                JsonNode fieldValue = content.get(field);
                resultsMap.get(field).add(fieldValue != null ? fieldValue.asInt() : 0);

            });
        });

        ObjectNode result = OBJECT_MAPPER.createObjectNode();
        result.set(LABEL, OBJECT_MAPPER.valueToTree(labels));
        resultsMap.forEach((key, value) -> result.set(key, OBJECT_MAPPER.valueToTree(value)));
        return result;
    }

    @Override
    protected JsonNode processNode(JsonNode currentNode, Map<String, String> extraValues)
    {
        return null;
    }
}

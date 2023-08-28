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

    @Override
    protected ArrayNode processObjectNode(JsonNode jsonNode, String filterField, String filterValue)
        throws JsonProcessingException
    {
        ArrayNode arrayNode = OBJECT_MAPPER.createArrayNode();
        Iterator<String> fieldNames = jsonNode.fieldNames();

        while (fieldNames.hasNext()) {
            String date = fieldNames.next();
            JsonNode childNode = jsonNode.get(date);

            if (childNode.get(0) == null) {
                arrayNode.add(OBJECT_MAPPER.readTree(String.format("{\"%s\"  : \"%s\"}", DATE, date)));
            }

            for (JsonNode node : childNode) {
                if ((filterField == null || filterValue == null || filterValue.equals(
                    node.get(filterField).asText())) &&  node != null)
                {
                    ((ObjectNode) node).put(DATE, date);
                    arrayNode.add(node);
                }
            }
        }
        return arrayNode;
    }
}

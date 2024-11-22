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
package com.xwiki.analytics.internal.aggregators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;

/**
 * Handles the sort and filtration for the livedata.
 *
 * @version $Id$
 * @since 1.13
 */
@Component(roles = AggregatorDataHandler.class)
@Singleton
public class AggregatorDataHandler
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Inject
    private Provider<XWikiContext> contextProvider;

    /**
     * Process the data stored locally.
     * @param dataSource page where the data is stored
     * @param order in which order you want the data asc/desc
     * @param sortField filed after you want to sort
     * @param filters map where the keys are the fields and the values are the value you want to filter after
     * @param pageSize how many entries are per page
     * @param pageCount the current page offset
     * @return a pair with the size of all the elements, a slice for with jsons for the current page
     * @throws JsonProcessingException
     * @throws XWikiException
     */
    public Pair<Integer, List<JsonNode>> handleData(DocumentReference dataSource, String order, String sortField,
        Map<String, String> filters, int pageSize, int pageCount) throws JsonProcessingException, XWikiException
    {
        XWikiDocument document = contextProvider.get().getWiki().getDocument(dataSource, contextProvider.get());
        String data = document.getContent();
        List<JsonNode> nodeList = OBJECT_MAPPER.readValue(data,
            OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, JsonNode.class));
        // Convert the order too boolean to avoid string comparison down the line. IDK if is better to have this
        // parameter as a boolean from the start and do the conversion in velocity. TODO maybe fix in the future.
        boolean asc = !order.equals("desc");

        // Apply filtering if necessary
        List<JsonNode> filteredList = !filters.isEmpty() ? filterData(nodeList, filters) : nodeList;

        // Apply sorting if necessary
        if (sortField != null && !sortField.isEmpty()) {
            sortData(filteredList, sortField, asc);
        }

        int totalSize = filteredList.size();

        // Paginate the data
        filteredList = paginateData(filteredList, pageSize, pageCount);

        return new ImmutablePair<>(totalSize, filteredList);
    }

    private List<JsonNode> paginateData(List<JsonNode> nodeList, int pageSize, int pageCount)
    {
        if (nodeList == null || nodeList.isEmpty() || pageSize <= 0 || pageCount < 0) {
            return Collections.emptyList();
        }

        int startIndex = pageSize * pageCount;
        int endIndex = Math.min(startIndex + pageSize, nodeList.size());

        // Check if startIndex is out of bounds
        if (startIndex >= nodeList.size()) {
            return Collections.emptyList();
        }

        return nodeList.subList(startIndex, endIndex);
    }

    private List<JsonNode> filterData(List<JsonNode> nodeList, Map<String, String> filters)
    {
        List<JsonNode> filteredList = new ArrayList<>();

        for (JsonNode node : nodeList) {
            boolean matchesAllFilters = true;

            for (String key : filters.keySet()) {

                if (!node.has(key) || !node.get(key).asText().contains(filters.get(key))) {
                    matchesAllFilters = false;
                    break;
                }
            }

            if (matchesAllFilters) {
                filteredList.add(node);
            }
        }

        return filteredList;
    }

    private void sortData(List<JsonNode> nodeList, String sortField, boolean asc)
    {
        nodeList.sort((node1, node2) -> {
            JsonNode value1 = node1.get(sortField);
            JsonNode value2 = node2.get(sortField);

            if (value1 == null || value2 == null) {
                return 0;
            }

            String text1 = value1.asText();
            String text2 = value2.asText();

            int comparison = text1.compareTo(text2);
            return asc ? comparison : -comparison;
        });
    }
}

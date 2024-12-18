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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.inject.Provider;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link AggregatorDataHandlerTest}
 *
 * @version $Id$
 */
@ComponentTest
public class AggregatorDataHandlerTest
{
    @InjectMockComponents
    AggregatorDataHandler aggregatorDataHandler;

    @MockComponent
    private Provider<XWikiContext> contextProvider;

    @MockComponent
    private XWikiContext wikiContext;

    @MockComponent
    private XWiki xWiki;

    @MockComponent
    private AggregatorDispatcher aggregatorDispatcher;

    @Mock
    private DocumentReference documentReference;

    @BeforeEach
    void setup() throws XWikiException
    {
        String JSON = "[{\"serverTimestamp\":2,\"serverDatePretty\":\"2024-10-31\"},{\"serverTimestamp\":1,"
            + "\"serverDatePretty\":\"2024-20-31\"}]";
        when(contextProvider.get()).thenReturn(wikiContext);
        when(wikiContext.getWiki()).thenReturn(xWiki);
        when(aggregatorDispatcher.getSaveLocation(any(), any())).thenReturn(documentReference);
        XWikiDocument doc = new XWikiDocument(new DocumentReference("test", "test", "Test"));
        doc.setContent(JSON);
        when(this.xWiki.getDocument(any(DocumentReference.class), any())).thenReturn(doc);
    }

    /**
     * Check that the data handle can sort the json provided.
     *
     * @throws XWikiException
     * @throws JsonProcessingException
     */
    @Test
    void handleDataSortTest() throws XWikiException, JsonProcessingException
    {
        Pair<Integer, List<JsonNode>> result =
            aggregatorDataHandler.handleData("test", "asc", "serverTimestamp", new HashMap<>(), 1, 1);

        Assertions.assertEquals(result.getLeft().intValue(), 2);
        Assertions.assertEquals(result.getRight().size(), 1);
        Assertions.assertEquals(result.getRight().get(0).get("serverTimestamp").asInt(), 2);
    }

    /**
     * Check that the data handle can paginate the data.
     *
     * @throws XWikiException
     * @throws JsonProcessingException
     */
    @Test
    void handleDataPaginationTest() throws XWikiException, JsonProcessingException
    {
        Pair<Integer, List<JsonNode>> result =
            aggregatorDataHandler.handleData("test", "asc", "serverTimestamp", new HashMap<>(), 1, 0);

        Assertions.assertEquals(result.getLeft().intValue(), 2);
        Assertions.assertEquals(result.getRight().size(), 1);
        Assertions.assertEquals(result.getRight().get(0).get("serverTimestamp").asInt(), 1);
    }

    /**
     * Check that the data handle can filter the data.
     *
     * @throws XWikiException
     * @throws JsonProcessingException
     */
    @Test
    void handleDataFilterTest() throws XWikiException, JsonProcessingException
    {
        Pair<Integer, List<JsonNode>> result = aggregatorDataHandler.handleData("test", "asc", "serverTimestamp",
            Collections.singletonMap("serverDatePretty", "2024-20-31"), 2, 0);

        Assertions.assertEquals(result.getLeft().intValue(), 1);
        Assertions.assertEquals(result.getRight().size(), 1);
        Assertions.assertEquals(result.getRight().get(0).get("serverDatePretty").asText(), "2024-20-31");
    }
}

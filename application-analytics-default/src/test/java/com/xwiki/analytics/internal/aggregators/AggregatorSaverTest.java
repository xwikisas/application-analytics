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

import java.util.List;

import javax.inject.Provider;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.xwiki.component.util.ReflectionUtils;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link AggregatorSaver}
 *
 * @version $Id$
 */
@ComponentTest
class AggregatorSaverTest
{
    @InjectMockComponents
    private AggregatorSaver aggregatorSaver;

    @MockComponent
    private Provider<XWikiContext> contextProvider;

    @MockComponent
    private XWikiContext xWikiContext;

    @MockComponent
    private XWiki xWiki;

    @MockComponent
    private Logger logger;

    /**
     * Tests that the data is saved.
     */
    @Test
    void saveDataDirty() throws JsonProcessingException, XWikiException
    {
        ReflectionUtils.setFieldValue(this.aggregatorSaver, "logger", this.logger);
        String JSON = "[{\"serverTimestamp\":1698710400,\"serverDatePretty\":\"2024-10-31\"}]";
        ObjectMapper mapper = new ObjectMapper();
        when(this.contextProvider.get()).thenReturn(xWikiContext);
        when(this.xWikiContext.getWiki()).thenReturn(this.xWiki);
        when(this.xWikiContext.getWikiId()).thenReturn("test");
        XWikiDocument doc = new XWikiDocument(new DocumentReference("test", "test", "Test"));
        when(this.xWiki.getDocument(any(DocumentReference.class), any())).thenReturn(doc);
        aggregatorSaver.saveData((ArrayNode) mapper.readTree(JSON), List.of("testSPace", "Test"), "Page");
        doc.setContentDirty(false);
        Assertions.assertEquals(doc.getContent(), JSON);
        aggregatorSaver.saveData((ArrayNode) mapper.readTree(JSON), List.of("testSPace", "Test"), "Page");
        verify(logger).warn("The local cache is still up to date and it wasn't updated!");
    }
}

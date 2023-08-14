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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.xwiki.resource.ResourceReferenceResolver;
import org.xwiki.resource.ResourceType;
import org.xwiki.resource.ResourceTypeResolver;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;
import org.xwiki.url.ExtendedURL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.stream.JsonReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link MatomoAnalyticsManager}
 *
 * @version $Id$
 */
@ComponentTest
public class MostViewedJsonNormaliserTest
{
    @InjectMockComponents
    private MostViewedJsonNormaliser mostViewedJsonNormaliser;

    @MockComponent
    private ResourceReferenceResolver<ExtendedURL> resourceReferenceResolver;

    @MockComponent
    private ResourceTypeResolver<ExtendedURL> resourceTypeResolver;

    private static JsonNode node;

    @Test
    public void testObjectJSONResponse() throws Exception
    {
        readJSONS();
        setupURLs("http://localhost:8080/xwiki/bin/view/Analytics/",
            "http://localhost:8080/xwiki/bin/view/Analytics/Code/Macros/MostViewedPage");
        assertEquals(node.get("ResponseObjectJSON"),
            mostViewedJsonNormaliser.normaliseData(node.get("ObjectJSON").toString()));
    }

    @Test
    public void testArrayJSONResponse() throws Exception
    {
        readJSONS();
        setupURLs("http://localhost:8080/xwiki/bin/view/Analytics/Code/MostViwedPages",
            "http://localhost:8081/xwiki/bin/view/Main/");
        assertEquals(node.get("ResponseArrayJSON"),
            mostViewedJsonNormaliser.normaliseData(node.get("ArrayJSONS").toString()));
    }

    private void setupURLs(String url1, String url2) throws Exception
    {
        ExtendedURL extendedURL1 = new ExtendedURL(new URL(url1), null);
        ExtendedURL extendedURL2 = new ExtendedURL(new URL(url2), null);

        ResourceType resourceType1 = resourceTypeResolver.resolve(extendedURL1, Collections.emptyMap());
        ResourceType resourceType2 = resourceTypeResolver.resolve(extendedURL2, Collections.emptyMap());

        when(resourceReferenceResolver.resolve(extendedURL1, resourceType1, Collections.emptyMap())).thenReturn(null);
        when(resourceReferenceResolver.resolve(extendedURL2, resourceType2, Collections.emptyMap())).thenReturn(null);
    }

    private void readJSONS() throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream is = JsonReader.class.getResourceAsStream("/tests.json");
        node = objectMapper.readTree(is);
    }
}

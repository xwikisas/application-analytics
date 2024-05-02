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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Named;
import javax.inject.Provider;

import org.apache.http.HttpEntity;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.xwiki.component.util.ReflectionUtils;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import com.xwiki.analytics.JsonNormaliser;
import com.xwiki.analytics.configuration.AnalyticsConfiguration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

/**
 * Unit test for {@link MatomoAnalyticsManager}
 *
 * @version $Id$
 */
@ComponentTest
public class MatomoAnalyticsManagerTest
{
    @InjectMockComponents
    private MatomoAnalyticsManager matomoAnalyticsManager;

    @MockComponent
    @Named("MostViewedPages")
    private JsonNormaliser jsonNormaliser;

    @MockComponent
    private AnalyticsConfiguration configuration;

    @MockComponent
    private Logger logger;

    @MockComponent
    private Provider<List<JsonNormaliser>> jsonNormalizerProvider;

    @MockComponent
    private HttpClientBuilderFactory builderFactory;

    /**
     * Will test the Manager with a valid hint.
     */
    @Test
    void requestDataWithCorrectHintForNormaliser() throws IOException
    {
        List<JsonNormaliser> normalisers = new ArrayList<>();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse mockResponse = mock(HttpResponse.class);
        HttpEntity mockEntity = mock(HttpEntity.class);

        normalisers.add(this.jsonNormaliser);
        when(this.configuration.getAuthenticationToken()).thenReturn("token");
        when(this.configuration.getRequestAddress()).thenReturn("https://matomo-url/");
        when(this.configuration.getIdSite()).thenReturn("3");
        when(this.jsonNormalizerProvider.get()).thenReturn(normalisers);
        when(this.jsonNormaliser.getIdentifier()).thenReturn(MostViewedJsonNormaliser.HINT);
        when(this.builderFactory.create()).thenReturn(mockClient);
        when(mockClient.execute(any())).thenReturn(mockResponse);
        when(mockResponse.getEntity()).thenReturn(mockEntity);
        this.matomoAnalyticsManager.requestData(new HashMap<>(), new HashMap<>(), MostViewedJsonNormaliser.HINT);
        verify(this.jsonNormaliser).normaliseData(any(), eq(new HashMap<>()));
    }

    /**
     * Will test that an error happens if the user sets the parameters to be equal with null.
     */
    @Test
    void requestDataWithNullParameters()
    {
        ReflectionUtils.setFieldValue(this.matomoAnalyticsManager, "logger", this.logger);
        when(configuration.getAuthenticationToken()).thenReturn("token");
        when(configuration.getRequestAddress()).thenReturn("https://matomo-url/");
        when(configuration.getIdSite()).thenReturn("3");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            matomoAnalyticsManager.requestData(null, new HashMap<>(), MostViewedJsonNormaliser.HINT);
        });
        assertEquals("Error occurred while retrieving Matomo statistic results.", exception.getMessage());
        verify(logger).warn("Parameters must not be null.");
    }

    /**
     * Will test the Manager with an invalid hint
     */
    @Test
    void requestDataWithInvalidNormaliser()
    {
        ReflectionUtils.setFieldValue(this.matomoAnalyticsManager, "logger", this.logger);
        when(this.configuration.getAuthenticationToken()).thenReturn("token");
        when(this.configuration.getRequestAddress()).thenReturn("https://matomo-url/");
        when(this.configuration.getIdSite()).thenReturn("3");
        when(this.jsonNormalizerProvider.get()).thenReturn(new ArrayList<>());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            matomoAnalyticsManager.requestData(new HashMap<>(), new HashMap<>(), "RANDOM_NORMALISER");
        });
        assertEquals("Error occurred while retrieving Matomo statistic results.", exception.getMessage());
        verify(logger).warn("There is no JSON normalizer associated with the [{}] hint you provided.",
            "RANDOM_NORMALISER");
    }
}

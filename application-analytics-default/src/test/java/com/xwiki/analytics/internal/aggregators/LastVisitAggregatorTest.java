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

import javax.inject.Named;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.xwiki.component.util.ReflectionUtils;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xwiki.analytics.configuration.AggregatorConfiguration;
import com.xwiki.analytics.configuration.AnalyticsConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link LastVisitAggregator}
 *
 * @version $Id$
 */
@ComponentTest
public class LastVisitAggregatorTest
{

    @InjectMockComponents
    @Spy
    private LastVisitAggregator lastVisitAggregator;

    @MockComponent
    private AnalyticsConfiguration configuration;

    @MockComponent
    @Named("last_visit")
    private AggregatorConfiguration lastSeenAggregatorConfigs;


    @MockComponent
    private Logger logger;


    /**
     * Tests the value returned by the getHint method.
     */
    @Test
    void getHintTest()
    {
        assertEquals(lastVisitAggregator.getHint(), "last_visit");
    }

    /**
     * Tests the value returned by the getSpace method.
     */
    @Test
    void getSpaceTes()
    {
        assertEquals(lastVisitAggregator.getSpace(), List.of("Analytics", "Code", "AggregateData"));
    }

    /**
     * Tests the value returned by the getSorePage method.
     */
    @Test
    void getStorePage()
    {
        assertEquals(lastVisitAggregator.getPage(), "LastVisitStorage");
    }

    /**
     * Tests the aggregateData method.
     */
    @Test
    void aggregateDataTest() throws Exception
    {

        ReflectionUtils.setFieldValue(this.lastVisitAggregator, "logger", this.logger);
        when(configuration.getIdSite()).thenReturn("testSite");
        when(configuration.getAuthenticationToken()).thenReturn("testToken");
        when(configuration.getRequestAddress()).thenReturn("http://example.com");
        when(lastSeenAggregatorConfigs.getTimeIntervalForStatistics()).thenReturn("week");
        when(lastVisitAggregator.computeInterval(lastSeenAggregatorConfigs)).thenReturn("2024-01-01,2024-12-31");

        // Mock HTTP request responses
        String userResponse = "[{\"idvisitor\":\"1234\",\"label\":\"Test User\"}]";
        String visitResponse = "[{\"serverTimestamp\":1698710400,\"serverDatePretty\":\"2024-10-31\"}]";

        doReturn(userResponse).when(lastVisitAggregator)
            .makeHttpRequest(Mockito.argThat(uri -> uri.toString().contains("getUsers")));

        doReturn(visitResponse).when(lastVisitAggregator)
            .makeHttpRequest(Mockito.argThat(uri -> uri.toString().contains("getLastVisitsDetails")));

        // Used to capture the local variable that was sent to the aggregatorSaver.
        ArgumentCaptor<ArrayNode> captor = ArgumentCaptor.forClass(ArrayNode.class);
        lastVisitAggregator.aggregateData();

        // Assert
        verify(logger).warn("Started aggregating data");
        // Use the captor to see the result json array
        verify(lastVisitAggregator.aggregatorSaver).saveData(captor.capture(), anyList(), anyString());
        // Verify the saved data
        ArrayNode result = captor.getValue();
        assertEquals(1, result.size());
        ObjectNode data = (ObjectNode) result.get(0);
        assertEquals("Test User", data.get("label").asText());
        assertEquals(1698710400, data.get("serverTimestamp").asLong());
        assertEquals("2024-10-31", data.get("serverDatePretty").asText());
    }

    /**
     * Tests the computeInterval method for the custom case.
     */
    @Test
    void testComputeInterval()
    {
        when(lastSeenAggregatorConfigs.getTimeIntervalForStatistics()).thenReturn("custom");
        when(lastSeenAggregatorConfigs.getStartDate()).thenReturn("2024-11-01 00:00");
        when(lastSeenAggregatorConfigs.getEndDate()).thenReturn("2024-11-26 23:59");
        assertEquals("2024-11-01,2024-11-26", lastVisitAggregator.computeInterval(lastSeenAggregatorConfigs));
    }
}

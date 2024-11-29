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
package com.xwiki.analytics.internal.configuration;

import javax.inject.Named;

import org.junit.jupiter.api.Test;
import org.xwiki.configuration.ConfigurationSource;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link LastSeenAggregatorConfigs}
 *
 * @version $Id$
 */
@ComponentTest
class LastSeenAggregatorConfigurationTest
{
    @InjectMockComponents
    private LastSeenAggregatorConfigs lastSeenAggregatorConfigs;

    @MockComponent
    @Named("analyticsAggregatorLastSeen")
    private ConfigurationSource aggregatorConfiguration;


    @Test
    void getTimeIntervalForStatisticsTest()
    {
        when(this.aggregatorConfiguration.getProperty("timeIntervalForStatistics", "year")).thenReturn("year");
        assertEquals("year", this.lastSeenAggregatorConfigs.getTimeIntervalForStatistics());
    }

    @Test
    void getStartDateTest()
    {
        when(this.aggregatorConfiguration.getProperty("startDate", "")).thenReturn("2016-01-01");
        assertEquals("2016-01-01", this.lastSeenAggregatorConfigs.getStartDate());
    }

    @Test
    void getEndDateTest(){
        when(this.aggregatorConfiguration.getProperty("endDate", "")).thenReturn("2016-01-01");
        assertEquals("2016-01-01", this.lastSeenAggregatorConfigs.getEndDate());
    }



}

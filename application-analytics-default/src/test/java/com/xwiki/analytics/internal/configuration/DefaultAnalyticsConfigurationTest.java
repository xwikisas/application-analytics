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
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link DefaultAnalyticsConfiguration}
 *
 * @version $Id$
 */
@ComponentTest
public class DefaultAnalyticsConfigurationTest
{
    @InjectMockComponents
    private DefaultAnalyticsConfiguration defaultAnalyticsConfiguration;

    @MockComponent
    @Named("analytics")
    private ConfigurationSource analyticsConfigurationSource;

    @Test
    public void getAuthenticationTokenTest()
    {
        when(this.analyticsConfigurationSource.getProperty("authToken", "")).thenReturn("token");
        assertEquals("token", this.defaultAnalyticsConfiguration.getAuthenticationToken());
    }

    @Test
    public void getSiteIDTest()
    {
        when(this.analyticsConfigurationSource.getProperty("siteId", "")).thenReturn("3");
        assertEquals("3", this.defaultAnalyticsConfiguration.getIdSite());
    }

    @Test
    public void getAddressTest()
    {
        when(this.analyticsConfigurationSource.getProperty("requestAddress", "")).thenReturn("13.192.200.19");
        assertEquals("13.192.200.19", this.defaultAnalyticsConfiguration.getRequestAddress());
    }
}

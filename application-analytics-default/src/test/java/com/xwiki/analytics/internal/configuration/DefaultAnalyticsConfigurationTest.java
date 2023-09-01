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
import org.slf4j.Logger;
import org.xwiki.component.util.ReflectionUtils;
import org.xwiki.configuration.ConfigurationSource;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
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

    @MockComponent
    private Logger logger;

    @Test
    public void getAuthenticationToken()
    {
        when(this.analyticsConfigurationSource.getProperty("authToken", "")).thenReturn("token");
        assertEquals("token", this.defaultAnalyticsConfiguration.getAuthenticationToken());
    }

    @Test
    public void getSiteID()
    {
        when(this.analyticsConfigurationSource.getProperty("siteId", "")).thenReturn("3");
        assertEquals("3", this.defaultAnalyticsConfiguration.getIdSite());
    }

    @Test
    public void getAddress()
    {
        when(this.analyticsConfigurationSource.getProperty("requestAddress", "")).thenReturn("13.192.200.19");
        assertEquals("13.192.200.19", this.defaultAnalyticsConfiguration.getRequestAddress());
    }

    /**
     * Will test to see if the function will throw an error when the request address is null
     */
    @Test
    public void missingRequestAddress()
    {
        ReflectionUtils.setFieldValue(this.defaultAnalyticsConfiguration, "logger", this.logger);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            this.defaultAnalyticsConfiguration.getRequestAddress();
        });
        assertEquals("The requestAddress is missing.", exception.getMessage());
    }

    /**
     * Will test to see if the function will throw an error when the id site is null
     */
    @Test
    public void missingIdSite()
    {
        ReflectionUtils.setFieldValue(this.defaultAnalyticsConfiguration, "logger", this.logger);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            this.defaultAnalyticsConfiguration.getIdSite();
        });
        assertEquals("The siteId is missing.", exception.getMessage());
    }

    /**
     * Will test to see if the function will throw an error when the auth token is null
     */
    @Test
    public void missingAuthToken()
    {
        ReflectionUtils.setFieldValue(this.defaultAnalyticsConfiguration, "logger", this.logger);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            this.defaultAnalyticsConfiguration.getAuthenticationToken();
        });
        assertEquals("The authToken is missing.", exception.getMessage());
    }

    /**
     * Will test to see if the function will throw an error when the request address is empty
     */
    @Test
    public void returnDefaultValueRequestAddress()
    {
        when(this.analyticsConfigurationSource.getProperty("requestAddress", "")).thenReturn("");
        ReflectionUtils.setFieldValue(this.defaultAnalyticsConfiguration, "logger", this.logger);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            this.defaultAnalyticsConfiguration.getRequestAddress();
        });
        assertEquals("The requestAddress is missing.", exception.getMessage());
    }

    /**
     * Will test to see if the function will throw an error when the id site is empty
     */
    @Test
    public void returnDefaultIdSite()
    {
        when(this.analyticsConfigurationSource.getProperty("siteId", "")).thenReturn("");
        ReflectionUtils.setFieldValue(this.defaultAnalyticsConfiguration, "logger", this.logger);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            this.defaultAnalyticsConfiguration.getIdSite();
        });
        assertEquals("The siteId is missing.", exception.getMessage());
    }

    /**
     * Will test to see if the function will throw an error when the auth token is empty
     */
    @Test
    public void returnDefaultAuthToken()
    {
        when(this.analyticsConfigurationSource.getProperty("authToken", "")).thenReturn("");
        ReflectionUtils.setFieldValue(this.defaultAnalyticsConfiguration, "logger", this.logger);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            this.defaultAnalyticsConfiguration.getAuthenticationToken();
        });
        assertEquals("The authToken is missing.", exception.getMessage());
    }
}

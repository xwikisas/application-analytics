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
import java.util.HashMap;

import javax.inject.Named;

import org.junit.jupiter.api.Test;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.resource.CreateResourceReferenceException;
import org.xwiki.resource.CreateResourceTypeException;
import org.xwiki.resource.UnsupportedResourceReferenceException;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import com.xwiki.analytics.JsonNormaliser;
import com.xwiki.analytics.configuration.AnalyticsConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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

    @Test
    public void MatomoAnalyticsManagerTest()
        throws ComponentLookupException, IOException, UnsupportedResourceReferenceException, InterruptedException,
        CreateResourceTypeException, CreateResourceReferenceException
    {
        when(this.configuration.getAuthenticationToken()).thenReturn("token");
        when(this.configuration.getRequestAddress()).thenReturn("http://130.61.233.19/matomo");
        when(this.configuration.getIdSite()).thenReturn("3");
        assertEquals(null,  this.matomoAnalyticsManager.requestData(new HashMap<>(), "MostViewedPages"));
    }
}

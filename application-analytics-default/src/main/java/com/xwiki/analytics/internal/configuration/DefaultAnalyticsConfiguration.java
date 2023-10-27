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

import java.text.MessageFormat;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.configuration.ConfigurationSource;
import org.xwiki.stability.Unstable;

import com.xwiki.analytics.configuration.AnalyticsConfiguration;

/**
 * Default implementation of {@link AnalyticsConfiguration}.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Singleton
@Unstable
public class DefaultAnalyticsConfiguration implements AnalyticsConfiguration
{
    @Inject
    @Named("analytics")
    private ConfigurationSource configDocument;

    @Inject
    private Logger logger;

    @Override
    public String getRequestAddress()
    {
        return this.configDocument.getProperty("requestAddress", "");
    }

    @Override
    public String getIdSite()
    {
        return this.configDocument.getProperty("siteId", "");
    }

    @Override
    public String getAuthenticationToken()
    {
        return this.configDocument.getProperty("authToken", "");
    }

    @Override
    public String getTrackingCode()
    {
        return this.configDocument.getProperty("trackingCode", "");
    }
}

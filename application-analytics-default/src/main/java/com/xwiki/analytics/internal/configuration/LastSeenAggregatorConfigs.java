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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.configuration.ConfigurationSource;
import org.xwiki.stability.Unstable;

import com.xwiki.analytics.configuration.AggregatorConfiguration;

/**
 * Config implementation for the last seen macro.
 *
 * @version $Id$
 * @since 1.2
 */
@Component
@Singleton
@Named("lastSeenConfig")
@Unstable
public class LastSeenAggregatorConfigs implements AggregatorConfiguration
{
    @Inject
    @Named("analyticsAggregatorLastSeen")
    private ConfigurationSource configDocument;

    @Override
    public String getTimeIntervalForStatistics()
    {
        return this.configDocument.getProperty("timeIntervalForStatistics", "year");
    }

    @Override
    public String getStartDate()
    {
        return this.configDocument.getProperty("startDate", "");
    }

    @Override
    public String getEndDate()
    {
        return this.configDocument.getProperty("endDate", "");
    }
}

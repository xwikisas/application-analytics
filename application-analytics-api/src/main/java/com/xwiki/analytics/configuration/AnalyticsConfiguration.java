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
package com.xwiki.analytics.configuration;

import org.xwiki.component.annotation.Role;
import org.xwiki.stability.Unstable;

/**
 * Configuration for the Analytics Application.
 *
 * @version $Id$
 * @since 1.2
 */
@Role
@Unstable
public interface AnalyticsConfiguration
{
    /**
     * @return the address where the requests will be made
     */
    String getRequestAddress();

    /**
     * @return the id of the site that we want to see the statistics for
     */
    String getIdSite();

    /**
     * @return the authentication token that permits to access the statistics
     */
    String getAuthenticationToken();

    /**
     * @return the tracking code for Matomo.
     */
    String getTrackingCode();

    /**
     * @return true if the tracking is enabled, false otherwise.
     * @since 1.0.2
     */
    boolean isEnabled();
}

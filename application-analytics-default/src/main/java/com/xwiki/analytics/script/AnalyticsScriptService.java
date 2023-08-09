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
package com.xwiki.analytics.script;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.script.service.ScriptService;
import org.xwiki.stability.Unstable;

import com.fasterxml.jackson.databind.JsonNode;
import com.xwiki.analytics.AnalyticsManager;
import com.xwiki.analytics.configuration.AnalyticsConfiguration;

/**
 * The scripting service for the Analytics App (Pro).
 *
 * @version $Id$
 * @since 1.1
 */
@Component
@Named("analytics")
@Unstable
@Singleton
public class AnalyticsScriptService implements ScriptService
{
    @Inject
    private AnalyticsConfiguration configuration;

    @Inject
    @Named("Matomo")
    private AnalyticsManager analyticsManager;

    /**
     * This Method is used to interrogate the Matomo API.
     *
     * @param jsonNormaliserHint hint to select the json normaliser.
     * @param parameterList a map of the parameters for the url
     * @return will return a json string.
     */
    public JsonNode getDataFromRequest(Map<String, String> parameterList, String jsonNormaliserHint)
    {
        parameterList.put("idSite", configuration.getIdSite());
        parameterList.put("token_auth", configuration.getAuthenticationToken());
        try {
            return analyticsManager.requestData(configuration.getRequestAddress(), parameterList, jsonNormaliserHint);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

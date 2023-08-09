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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.resource.CreateResourceReferenceException;
import org.xwiki.resource.CreateResourceTypeException;
import org.xwiki.resource.UnsupportedResourceReferenceException;

import com.fasterxml.jackson.databind.JsonNode;
import com.xwiki.analytics.AnalyticsManager;
import com.xwiki.analytics.JsonNormaliser;

/**
 * The Matomo request manager.This class will make request towards Matomo and return the data.
 *
 * @version $Id$
 */
@Component
@Named("Matomo")
@Singleton
public class MatomoAnalyticsManager implements AnalyticsManager
{
    @Inject
    private ComponentManager componentManager;

    /**
     * @param jsonNormaliserHint Hint to select the json normaliser.
     * @param address The address where the request will be made.
     * @param parameterList A list of key, value pairs that will represent the parameters for the request.
     * @return Will return a json with all the data returned by Matomo.
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public JsonNode requestData(String address, Map<String, String> parameterList, String jsonNormaliserHint)
        throws IOException, InterruptedException, ComponentLookupException, UnsupportedResourceReferenceException,
        CreateResourceTypeException, CreateResourceReferenceException
    {
        JsonNormaliser jsonNormaliser = componentManager.getInstance(JsonNormaliser.class, jsonNormaliserHint);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder(buildURI(address, parameterList)).build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return jsonNormaliser.normaliseData(response.body());
    }

    /**
     * This function will create the URI for the matomo request by appending all the necessary data.
     *
     * @param address Address of the Matomo server.
     * @param parameterList List of the url parameters.
     * @return The final URI in string format.
     */
    private URI buildURI(String address, Map<String, String> parameterList)
    {
        UriBuilder uriBuilder = UriBuilder.fromUri(address).path("index.php");

        if (parameterList != null && !parameterList.isEmpty()) {
            parameterList.forEach((k, v) -> uriBuilder.queryParam(k, v));
        }

        return uriBuilder.build();
    }
}

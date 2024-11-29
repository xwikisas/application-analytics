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

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.stability.Unstable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xwiki.analytics.configuration.AggregatorConfiguration;

/**
 * Aggregates the data between the user and last visit endpoints to create a json with all the users and their last
 * login date.
 *
 * @version $Id$
 * @since 1.1
 */
@Component
@Singleton
@Unstable
public class LastVisitAggregator extends AbstractAggregator
{
    protected static final String HINT = "last_visit";

    private static final String GET_USERS_METHOD = "UserId.getUsers";

    private static final String LAST_VISIT_ENDPOINT = "Live.getLastVisitsDetails";

    private static final String SHOW_COLUMNS = "showColumns";

    private static final String METHOD = "method";

    private static final String SERVER_TIMESTAMP = "serverTimestamp";

    private static final String LABEL = "label";

    private static final String SERVER_DATE_PRETTY = "serverDatePretty";

    private static final String LAST_VISIT_STORAGE = "LastVisitStorage";

    private static final List<String> SPACE = List.of("Analytics", "Code", "AggregateData");

    @Inject
    @Named("lastSeenConfig")
    private AggregatorConfiguration aggregatorConfiguration;

    @Inject
    private Logger logger;

    @Override
    public void aggregateData()
    {

        logger.warn("Started aggregating data");
        Map<String, String> commonQueryParameters = new HashMap<>();
        commonQueryParameters.put("module", "API");
        commonQueryParameters.put("format", "json");
        commonQueryParameters.put("idSite", this.matomoServerConfig.getIdSite());
        commonQueryParameters.put("token_auth", this.matomoServerConfig.getAuthenticationToken());
        commonQueryParameters.put("period", "range");
        commonQueryParameters.put("date", this.computeInterval(aggregatorConfiguration));

        Map<String, String> getUserParameters = new HashMap<>(commonQueryParameters);
        getUserParameters.put(METHOD, GET_USERS_METHOD);
        getUserParameters.put(SHOW_COLUMNS, "idvisitor,label");
        URI getUsers = this.buildBaseURI(this.matomoServerConfig.getRequestAddress(), getUserParameters);

        Map<String, String> lastVisitParameters = new HashMap<>(commonQueryParameters);
        lastVisitParameters.put(METHOD, LAST_VISIT_ENDPOINT);
        lastVisitParameters.put(SHOW_COLUMNS, "serverTimestamp,serverDatePretty");
        // A trick to get only the last visit.
        lastVisitParameters.put("filter_limit", "1");
        try {

            ArrayNode resultNode = this.processData(getUsers, lastVisitParameters);
            logger.warn("Finished aggregating data");
            this.aggregatorSaver.saveData(resultNode, SPACE, LAST_VISIT_STORAGE);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getHint()
    {
        return LastVisitAggregator.HINT;
    }

    @Override
    public String getPage()
    {
        return LAST_VISIT_STORAGE;
    }

    @Override
    public List<String> getSpace()
    {
        return SPACE;
    }

    private ArrayNode processData(URI getUser, Map<String, String> lastVisitParameters) throws JsonProcessingException
    {
        String usersJSON = makeHttpRequest(getUser);
        ArrayNode resultNode = OBJECT_MAPPER.createArrayNode();
        JsonNode jsonUserRoot = OBJECT_MAPPER.readTree(usersJSON);
        for (JsonNode users : jsonUserRoot) {
            String userId = users.get("idvisitor").asText();
            String segment = String.format("visitorId==%s", userId);
            lastVisitParameters.put("segment", segment);
            URI composedURI = this.buildBaseURI(matomoServerConfig.getRequestAddress(), lastVisitParameters);
            // The result is an array with only one element, and we need to get the node directly.
            JsonNode lastVisitDetails = OBJECT_MAPPER.readTree(this.makeHttpRequest(composedURI)).get(0);
            jsonBuilder(resultNode, users, lastVisitDetails);
        }
        return resultNode;
    }

    private void jsonBuilder(ArrayNode resultNode, JsonNode userNode, JsonNode lastVisitNode)
    {
        ObjectNode dataNode = OBJECT_MAPPER.createObjectNode();

        // Structure of dataNode:
        // { "label": "readableUserName",
        //   "serverTimestamp": "posix timestamp",
        //   "serverDatePretty": "pretty format of the timestamp"
        // }
        dataNode.put(LABEL, userNode.get(LABEL).asText());
        dataNode.put(SERVER_TIMESTAMP, lastVisitNode.get(SERVER_TIMESTAMP).asLong());
        dataNode.put(SERVER_DATE_PRETTY, lastVisitNode.get(SERVER_DATE_PRETTY).asText());
        resultNode.add(dataNode);
    }
}

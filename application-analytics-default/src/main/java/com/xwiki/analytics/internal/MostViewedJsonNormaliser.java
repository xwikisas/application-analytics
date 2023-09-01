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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.resource.CreateResourceReferenceException;
import org.xwiki.resource.CreateResourceTypeException;
import org.xwiki.resource.ResourceReference;
import org.xwiki.resource.ResourceReferenceResolver;
import org.xwiki.resource.ResourceType;
import org.xwiki.resource.ResourceTypeResolver;
import org.xwiki.resource.UnsupportedResourceReferenceException;
import org.xwiki.resource.entity.EntityResourceReference;
import org.xwiki.url.ExtendedURL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xwiki.analytics.JsonNormaliser;

import liquibase.repackaged.org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Implementation for {@link JsonNormaliser}.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Named("MostViewedPages")
@Singleton
public class MostViewedJsonNormaliser extends AbstractJsonNormaliser
{
    /**
     * Hint for the MostViewedJsonNormaliser.
     */
    public static final String HINT = "MostViewedPages";

    private static final String URL = "url";

    @Inject
    private ResourceReferenceResolver<ExtendedURL> resourceReferenceResolver;

    @Inject
    private ResourceTypeResolver<ExtendedURL> resourceTypeResolver;

    /**
     * Process the current node and add it to the final array of jsons.
     *
     * @param currentNode the current json that has to be processed
     */
    @Override
    protected JsonNode processNode(JsonNode currentNode)
    {

        if (currentNode.has(URL)) {
            this.handleURLNode((ObjectNode) currentNode);
        }
        return currentNode;
    }

    /**
     * Process the URL of a page to obtain the documentReference. This is necessary to retrieve the document name for
     * display when rendering the table.
     *
     * @param resourceReferenceURL the URL of the page
     * @return the reference associated to the given URL, or {@code null} in case it couldn't be resolved
     */
    private ResourceReference getResourceReferenceFromStringURL(String resourceReferenceURL)
    {
        try {

            // The URL provided by Matomo is an unencoded string. For utilization with the resourceTypeResolver,
            // this URL needs proper encoding. This is achieved by creating a URL object to split the URL into its
            // respective components and after that the URL is encoded using the URI constructor.
            URL url = new URL(resourceReferenceURL);
            URL encodedUrl = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
                url.getQuery(), url.getRef()).toURL();
            ExtendedURL extendedURL = new ExtendedURL(encodedUrl, null);
            ResourceType resourceType = this.resourceTypeResolver.resolve(extendedURL, Collections.emptyMap());
            return this.resourceReferenceResolver.resolve(extendedURL, resourceType, Collections.emptyMap());
        } catch (MalformedURLException | CreateResourceReferenceException | CreateResourceTypeException
                 | UnsupportedResourceReferenceException | URISyntaxException e) {
            this.logger.warn("Failed to get resource reference from URL: [{}]. Caused by [{}]", resourceReferenceURL,
                ExceptionUtils.getRootCauseMessage(e));
            return null;
        }
    }

    /**
     * Change the label node to contain the actual page name instead of an URL.
     *
     * @param objNode a json object
     */
    private void handleURLNode(ObjectNode objNode)
    {
        EntityResourceReference entityResourceReference =
            (EntityResourceReference) this.getResourceReferenceFromStringURL(objNode.get(URL).asText());
        if (entityResourceReference != null) {
            objNode.put(LABEL, entityResourceReference.getEntityReference().getName());
        }
    }
}

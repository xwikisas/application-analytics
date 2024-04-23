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

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.EntityReferenceResolver;
import org.xwiki.security.authorization.ContextualAuthorizationManager;
import org.xwiki.security.authorization.Right;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xwiki.analytics.JsonNormaliser;

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
    @Named("resource/standardURL")
    private EntityReferenceResolver<String> urlToReferenceResolver;

    @Inject
    private ContextualAuthorizationManager contextualAuthorizationManager;


    @Override
    public String getIdentifier()
    {
        return MostViewedJsonNormaliser.HINT;
    }

    @Override
    protected JsonNode processNode(JsonNode currentNode, Map<String, String> extraValues)
    {
        if (!currentNode.has(URL)) {
            return currentNode;
        }
        EntityReference pageReference = getPageReferenceFromUrl((ObjectNode) currentNode);
        if (pageReference == null) {
            return null;
        }
        updateNodeLabel((ObjectNode) currentNode, pageReference);
        return currentNode;
    }

    /**
     * Get the page reference from the URL.
     *
     * @param objectNode a JSON object
     * @return reference of the page if the resolver returned a valid value, otherwise {@code null}
     */
    private EntityReference getPageReferenceFromUrl(ObjectNode objectNode)
    {
        String url = objectNode.get(URL).asText();
        EntityReference entityReference = this.urlToReferenceResolver.resolve(url, EntityType.DOCUMENT);
        if (entityReference == null || !contextualAuthorizationManager.hasAccess(Right.VIEW, entityReference)) {
            return null;
        }
        return entityReference;
    }

    /**
     * Replace the url with the actual title of the page.
     *
     * @param objectNode a JSON object
     * @param entityReference reference of the page
     */
    private void updateNodeLabel(ObjectNode objectNode, EntityReference entityReference)
    {
        String pageName = getPageName(entityReference);
        objectNode.put(LABEL, pageName);
    }

    /**
     * Gets the name of the page if the page name is WebHome returns the name of the parent.
     *
     * @param entityReference reference of the page
     * @return name of the page
     */
    private String getPageName(EntityReference entityReference)
    {
        return entityReference.getName().equals("WebHome") ? entityReference.getParent().getName()
            : entityReference.getName();
    }
}

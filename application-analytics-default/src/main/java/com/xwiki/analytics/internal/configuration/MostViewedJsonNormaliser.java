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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xwiki.analytics.JsonNormaliser;

/**
 * The normaliser for the MostViewedMacro.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Named("MostViewed")
@Singleton
public class MostViewedJsonNormaliser implements JsonNormaliser
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String DATE = "date";

    private static final String URL = "url";

    private static final String LABEL = "label";

    private static final String DOCUMENT_REFERENCE = "documentReference";

    @Inject
    private ResourceReferenceResolver<ExtendedURL> resourceReferenceResolver;

    @Inject
    private ResourceTypeResolver<ExtendedURL> resourceTypeResolver;

    /**
     * This method will normalise the jsons returned by Matomo into a single format.
     *
     * @param jsonString the json provided by Matomo.
     * @return the normalised json as a string.
     */
    public JsonNode normaliseData(String jsonString)
        throws JsonProcessingException, MalformedURLException, UnsupportedResourceReferenceException,
        CreateResourceTypeException, CreateResourceReferenceException
    {
        JsonNode jsonNode = OBJECT_MAPPER.readTree(jsonString);
        if (jsonNode.isArray()) {
            processArrayNode(jsonNode);
        } else {
            jsonNode = processObjectNode(jsonNode);
        }
        return jsonNode;
    }

    private void processArrayNode(JsonNode jsonNode)
        throws MalformedURLException, UnsupportedResourceReferenceException, CreateResourceTypeException,
        CreateResourceReferenceException
    {
        for (JsonNode objNode : jsonNode) {
            if (objNode.isObject()) {
                ((ObjectNode) objNode).put(DATE, "");
                if (objNode.has(URL)) {
                    EntityResourceReference entityResourceReference =
                        (EntityResourceReference) this.getReference(objNode.get(URL).asText());
                    ((ObjectNode) objNode).put(LABEL,
                        entityResourceReference.getEntityReference().getName());
                    ((ObjectNode) objNode).put(DOCUMENT_REFERENCE,
                        entityResourceReference.getEntityReference().toString());
                }
            }
        }
    }

    private ArrayNode processObjectNode(JsonNode jsonNode)
        throws MalformedURLException, UnsupportedResourceReferenceException, CreateResourceTypeException,
        CreateResourceReferenceException
    {
        ArrayNode arrayNode = OBJECT_MAPPER.createArrayNode();
        Iterator<String> fieldNames = jsonNode.fieldNames();
        while (fieldNames.hasNext()) {
            String date = fieldNames.next();
            JsonNode childNode = jsonNode.get(date);
            for (JsonNode objNode : childNode) {
                if (objNode.isObject()) {
                    ((ObjectNode) objNode).put(DATE, date);
                    if (objNode.has(URL)) {
                        EntityResourceReference entityResourceReference =
                            (EntityResourceReference) this.getReference(objNode.get(URL).asText());
                        ((ObjectNode) objNode).put(LABEL,
                            entityResourceReference.getEntityReference().getName());
                        arrayNode.add(objNode);
                        ((ObjectNode) objNode).put(DOCUMENT_REFERENCE,
                            entityResourceReference.getEntityReference().toString());
                    }
                }
            }
        }
        return arrayNode;
    }

    private ResourceReference getReference(String url)
        throws MalformedURLException, CreateResourceTypeException, UnsupportedResourceReferenceException,
        CreateResourceReferenceException
    {
        ResourceReference result = null;
        ExtendedURL extendedURL = new ExtendedURL(new URL(url), null);
        ResourceType resourceType = this.resourceTypeResolver.resolve(extendedURL, Collections.emptyMap());
        result = this.resourceReferenceResolver.resolve(extendedURL, resourceType, Collections.emptyMap());

        return result;
    }
}

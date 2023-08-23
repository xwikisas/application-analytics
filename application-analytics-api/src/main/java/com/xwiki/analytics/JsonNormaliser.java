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
package com.xwiki.analytics;

import org.xwiki.component.annotation.Role;
import org.xwiki.stability.Unstable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Provides APIs for normalizing data received from Matomo, for easing the afterwards manipulations. Depending on what
 * was requested from Matomo, the response can have various formats, which are not consistent or need enhancement.
 *
 * @version $Id$
 * @since 1.0
 */
@Role
@Unstable
public interface JsonNormaliser
{
    /**
     * Normalise the data returned to have the expected format.
     *
     * @param jsonString a string that has the expected format
     * @return a {@link JsonNode} with the root of the normalized JSON
     * @throws JsonProcessingException Throws this error when the jsonString param is not a proper json
     */
    JsonNode normaliseData(String jsonString) throws JsonProcessingException;
}

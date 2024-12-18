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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.SpaceReference;
import org.xwiki.rendering.syntax.Syntax;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;

/**
 * Handles the storage of aggregated data.
 *
 * @version $Id$
 * @since 1.2
 */
@Component(roles = AggregatorSaver.class)
@Singleton
public class AggregatorSaver
{
    @Inject
    private Provider<XWikiContext> contextProvider;

    @Inject
    private Logger logger;

    /**
     * Saves the aggregated data on a xwiki page.
     *
     * @param data data that you want to store
     * @param spaces space where you want your data to be stored.
     * @param pageName name of the page where you want your data
     */
    public void saveData(ArrayNode data, List<String> spaces, String pageName)
    {
        XWikiContext context = contextProvider.get();
        XWiki xwiki = context.getWiki();
        SpaceReference spaceReference = new SpaceReference(context.getWikiId(), spaces);
        DocumentReference documentReference = new DocumentReference(pageName, spaceReference);
        try {
            XWikiDocument document = xwiki.getDocument(documentReference, context);
            document.setContent(data.toString());
            // Plain  text to make sure that we don't create a vulnerability.
            document.setSyntax(Syntax.PLAIN_1_0);
            if (!document.isContentDirty()) {
                logger.warn("The local cache is still up to date and it wasn't updated!");
            } else {
                xwiki.saveDocument(document, "Update the data", context);
            }
        } catch (XWikiException e) {
            logger.error("Error while saving aggregate data", e);
        }
    }
}

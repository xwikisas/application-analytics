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

import com.xpn.xwiki.XWikiContext;
import com.xwiki.analytics.Aggregator;

/**
 * Dispatches the correct aggregator and saving location based on a hint.
 *
 * @version $Id$
 * @since 1.2
 */
@Component(roles = AggregatorDispatcher.class)
@Singleton
public class AggregatorDispatcher
{
    private static final String INVALID_HINT_FOR_AGGREGATOR = "Invalid hint '[{}]' for aggregator.";

    @Inject
    private Provider<List<Aggregator>> aggregators;

    @Inject
    private Logger logger;

    /**
     * Return an aggregator based on a hint.
     * @param hint of the aggregator.
     * @return the aggregator associated with the @hint
     */
    public Aggregator getAggregator(String hint)
    {
        for (Aggregator aggregator : aggregators.get()) {
            if (aggregator.getHint().equals(hint)) {
                return aggregator;
            }
        }
        logger.warn(INVALID_HINT_FOR_AGGREGATOR, hint);
        return null;
    }

    /**
     * Returns the storage location an aggregator based on a hint.
     * @param hint hint of the aggregator.
     * @param context current context
     * @return document reference of associated with the storage of the aggregator.
     */
    public DocumentReference getSaveLocation(String hint, XWikiContext context)
    {
        for (Aggregator aggregator : aggregators.get()) {
            if (aggregator.getHint().equals(hint)) {
                return this.computeReference(aggregator, context);
            }
        }
        logger.warn(INVALID_HINT_FOR_AGGREGATOR, hint);

        return null;
    }

    private DocumentReference computeReference(Aggregator aggregator, XWikiContext context)
    {
        String pageName = aggregator.getPage();
        List<String> space = aggregator.getSpace();
        SpaceReference spaceReference = new SpaceReference(context.getWikiId(), space);
        return new DocumentReference(pageName, spaceReference);
    }
}

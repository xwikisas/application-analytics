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

import javax.inject.Provider;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import com.xpn.xwiki.XWikiContext;
import com.xwiki.analytics.Aggregator;

import static org.mockito.Mockito.when;

/**
 * Unit test for {@link AggregatorDispatcherTest}
 *
 * @version $Id$
 */
@ComponentTest
public class AggregatorDispatcherTest
{
    @MockComponent
    LastVisitAggregator lastVisitAggregator;

    @MockComponent
    private XWikiContext xWikiContext;

    @InjectMockComponents
    private AggregatorDispatcher aggregatorDispatcher;

    @MockComponent
    private Provider<List<Aggregator>> aggregators;

    @Test
    void testGetAggregator()
    {

        when(aggregators.get()).thenReturn(List.of(lastVisitAggregator));
        when(lastVisitAggregator.getHint()).thenReturn(LastVisitAggregator.HINT);
        Assertions.assertEquals(aggregatorDispatcher.getAggregator("last_visit"), lastVisitAggregator);
    }

    @Test
    void testGetSaveLocation()
    {
        when(aggregators.get()).thenReturn(List.of(lastVisitAggregator));
        when(lastVisitAggregator.getHint()).thenReturn(LastVisitAggregator.HINT);
        when(xWikiContext.getWikiId()).thenReturn("test");
        when(lastVisitAggregator.getPage()).thenReturn("page");
        when(lastVisitAggregator.getSpace()).thenReturn(List.of("space"));
        Assertions.assertEquals(
            this.aggregatorDispatcher.getSaveLocation(LastVisitAggregator.HINT, xWikiContext).toString(),
            "test:space.page");
    }
}

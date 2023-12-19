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
package com.xwiki.analytics.test.po;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.test.ui.XWikiWebDriver;
import org.xwiki.test.ui.po.ViewPage;

public class HomePageViewPage extends ViewPage
{
    private static XWikiWebDriver driver;

    public HomePageViewPage()
    {
        driver = getUtil().getDriver();
    }

    public static HomePageViewPage gotoPageHomePage()
    {
        getUtil().gotoPage("Analytics", "WebHome");
        return new HomePageViewPage();
    }

    public static HomePageEditPage gotoAndEdit()
    {
        DocumentReference documentReference = new DocumentReference("xwiki", "Analytics", "WebHome");
        Map<String, String> params = new HashMap<>();
        params.put("force", "1");
        getUtil().gotoPage(documentReference, "edit", params);
        return new HomePageEditPage();
    }

    /**
     * Calculates the number of gadgets that are present on the homepage of the application.
     * @return number of gadgets
     */
    public static int noOfGadgets()
    {
        return driver.findElements(By.className("gadget")).size();
    }
}

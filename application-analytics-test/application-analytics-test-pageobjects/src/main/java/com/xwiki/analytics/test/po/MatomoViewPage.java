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

import org.openqa.selenium.By;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.test.ui.XWikiWebDriver;
import org.xwiki.test.ui.po.ViewPage;

import static org.junit.Assert.fail;

/**
 * Encompass the process of creating a Matomo token using the browser interface provided by Matomo.
 */
public class MatomoViewPage extends ViewPage
{
    private static final String credentials = "ADMIN1";

    public MatomoViewPage()
    {

    }

    /**
     *  Creates a new access token by accesing the Matomo GUI.
     * @param address The address of the Matomo container.
     * @return the newly created token as a string
     */
    static public String createToken(String address)
    {
        XWikiWebDriver driver = getUtil().getDriver();
        getUtil().gotoPage(address);
        getUtil().gotoPage(
            address + "/index.php?module=UsersManager&action=addNewToken&idSite=1&period=day&date=2023" + "-09-03");
        driver.findElement(By.id("login_form_login")).sendKeys(credentials);
        driver.findElement(By.id("login_form_password")).sendKeys(credentials);
        driver.findElement(By.id("login_form_submit")).click();
        driver.findElement(By.id("login_form_password")).sendKeys(credentials);
        driver.findElement(By.id("login_form_submit")).click();
        driver.findElement(By.id("description")).sendKeys("TEST TOKEN");
        driver.findElement(By.cssSelector(".btn")).click();
        return driver.findElement(By.tagName("code")).getText();
    }
}

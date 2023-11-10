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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.xwiki.administration.test.po.AdministrationPage;
import org.xwiki.test.ui.XWikiWebDriver;
import org.xwiki.test.ui.po.ViewPage;

public class AdminViewPage extends ViewPage
{
    private static final String trackingCodeId = "Analytics.Code.ConfigurationClass_0_trackingCode";

    private static final String authTokenId = "Analytics.Code.ConfigurationClass_0_authToken";

    private static final String idSiteId = "Analytics.Code.ConfigurationClass_0_siteId";

    private static final String requestAddressId = "Analytics.Code.ConfigurationClass_0_requestAddress";

    public AdminViewPage()
    {

    }

    public static AdminViewPage gotoAdminPage()
    {
        AdministrationPage administrationPage = AdministrationPage.gotoPage();
        administrationPage.clickSection("Other", "Analytics");
        return new AdminViewPage();
    }

    public AdminViewPage setTrackingCode(XWikiWebDriver driver, String value)
    {
        driver.findElement(By.id(trackingCodeId)).sendKeys(value);
        return this;
    }

    public AdminViewPage setAuthTokenId(XWikiWebDriver driver, String value)
    {
        driver.findElement(By.id(authTokenId)).sendKeys(value);
        return this;
    }

    public AdminViewPage setIdSiteId(XWikiWebDriver driver, String value)
    {
        driver.findElement(By.id(idSiteId)).sendKeys(value);
        return this;
    }

    public AdminViewPage setRequestAddressId(XWikiWebDriver driver, String value)
    {
        driver.findElement(By.id(requestAddressId)).sendKeys(value);
        return this;
    }

    public AdminViewPage bringSaveButtonIntoView(XWikiWebDriver driver)
    {
        WebElement saveButton = driver.findElement(By.cssSelector(".btn.btn-primary"));
        Actions actions = driver.createActions();
        actions.moveToElement(saveButton).click().perform();
        return this;
    }

    public boolean inProgressNotification(String message)
    {

        try {
            this.waitForNotificationInProgressMessage(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean errorNotification(String message)
    {

        try {
            this.waitForNotificationErrorMessage(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean successNotification(String message)
    {
        try {
            this.waitForNotificationSuccessMessage(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

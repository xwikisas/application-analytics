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
    private static final String TRACKING_CODE_ID = "Analytics.Code.ConfigurationClass_0_trackingCode";

    private static final String AUTH_TOKEN_ID = "Analytics.Code.ConfigurationClass_0_authToken";

    private static final String IS_SITE = "Analytics.Code.ConfigurationClass_0_siteId";

    private static final String REQUEST_ADDRESS_ID = "Analytics.Code.ConfigurationClass_0_requestAddress";

    private final XWikiWebDriver driver;

    public AdminViewPage()
    {
        driver = getUtil().getDriver();
    }

    public AdminViewPage gotoAdminPage()
    {
        AdministrationPage administrationPage = AdministrationPage.gotoPage();
        administrationPage.clickSection("Other", "Analytics");
        return new AdminViewPage();
    }

    public AdminViewPage setTrackingCode(String value)
    {
        WebElement element = driver.findElement(By.id(TRACKING_CODE_ID));
        element.clear();
        element.sendKeys(value);
        return this;
    }

    public AdminViewPage setAuthTokenId(String value)
    {
        WebElement element = driver.findElement(By.id(AUTH_TOKEN_ID));
        element.clear();
        element.sendKeys(value);
        return this;
    }

    public AdminViewPage setIdSiteId(String value)
    {
        WebElement element = driver.findElement(By.id(IS_SITE));
        element.clear();
        element.sendKeys(value);
        return this;
    }

    public AdminViewPage setRequestAddressId(String value)
    {
        WebElement element = driver.findElement(By.id(REQUEST_ADDRESS_ID));
        element.clear();
        element.sendKeys(value);
        return this;
    }

    public AdminViewPage bringSaveButtonIntoView()
    {
        WebElement saveButton = driver.findElement(By.cssSelector(".btn.btn-primary"));
        Actions actions = driver.createActions();
        actions.moveToElement(saveButton).click().perform();
        return this;
    }

    public void inProgressNotification(String message)
    {
        this.waitForNotificationInProgressMessage(message);
    }

    public void errorNotification(String message)
    {
        this.waitForNotificationErrorMessage(message);
    }

    public void successNotification(String message)
    {
        this.waitForNotificationSuccessMessage(message);
    }
}

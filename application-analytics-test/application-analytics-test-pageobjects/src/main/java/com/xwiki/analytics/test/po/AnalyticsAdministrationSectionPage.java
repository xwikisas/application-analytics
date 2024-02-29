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
import org.openqa.selenium.support.FindBy;
import org.xwiki.administration.test.po.AdministrationPage;
import org.xwiki.test.ui.po.ViewPage;

/**
 * Responsible for interacting with the configuration tab of the application.
 */
public class AnalyticsAdministrationSectionPage extends ViewPage
{
    @FindBy(id = "Analytics.Code.ConfigurationClass_0_trackingCode")
    private WebElement tracking_code;

    @FindBy(id = "Analytics.Code.ConfigurationClass_0_authToken")
    private WebElement auth_token;

    @FindBy(id = "Analytics.Code.ConfigurationClass_0_siteId")
    private WebElement site;

    @FindBy(id = "Analytics.Code.ConfigurationClass_0_requestAddress")
    private WebElement request_address;

    public AnalyticsAdministrationSectionPage()
    {

    }

    public AnalyticsAdministrationSectionPage gotoPage()
    {
        AdministrationPage administrationPage = AdministrationPage.gotoPage();
        administrationPage.clickSection("Other", "Analytics");
        return new AnalyticsAdministrationSectionPage();
    }

    public AnalyticsAdministrationSectionPage setTrackingCode(String value)
    {
        tracking_code.clear();
        tracking_code.sendKeys(value);
        return this;
    }

    public AnalyticsAdministrationSectionPage setAuthTokenId(String value)
    {
        auth_token.clear();
        auth_token.sendKeys(value);
        return this;
    }

    public AnalyticsAdministrationSectionPage setIdSiteId(String value)
    {
        site.clear();
        site.sendKeys(value);
        return this;
    }

    public AnalyticsAdministrationSectionPage setRequestAddressId(String value)
    {
        request_address.clear();
        request_address.sendKeys(value);
        return this;
    }

    public AnalyticsAdministrationSectionPage saveConfigs()
    {
        WebElement saveButton = getUtil().getDriver().findElement(By.cssSelector(".btn.btn-primary"));
        Actions actions = getUtil().getDriver().createActions();
        actions.moveToElement(saveButton).click().perform();
        return this;
    }
}

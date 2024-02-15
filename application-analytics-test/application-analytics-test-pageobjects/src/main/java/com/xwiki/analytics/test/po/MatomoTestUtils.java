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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.xwiki.test.ui.XWikiWebDriver;

/**
 * Encompass the process of creating a Matomo token using the browser interface provided by Matomo.
 */
public class MatomoTestUtils {
    private static final String CREDENTIALS = "ADMIN1";
    private static final String LOGIN_FORM_LOGIN_ID = "login_form_login";
    private static final String LOGIN_FORM_PASSWORD_ID = "login_form_password";
    private static final String LOGIN_FORM_SUBMIT_ID = "login_form_submit";
    private static final String DESCRIPTION_ID = "description";
    private static final String BTN_CSS_SELECTOR = ".btn";
    private static final String TAG_NAME_CODE = "code";

    /**
     * Creates a new access token by accessing the Matomo GUI.
     *
     * @param address The address of the Matomo container.
     * @return the newly created token as a string
     */
    public static String createToken(String address, XWikiWebDriver driver) {
        driver.get(address + "/index.php?module=UsersManager&action=addNewToken&idSite=1&period=day&date=2023-09-03");
        driver.waitUntilElementIsVisible(By.id(LOGIN_FORM_LOGIN_ID));
        driver.findElement(By.id(LOGIN_FORM_LOGIN_ID)).sendKeys(CREDENTIALS);
        driver.waitUntilElementIsVisible(By.id(LOGIN_FORM_PASSWORD_ID));
        driver.findElement(By.id(LOGIN_FORM_PASSWORD_ID)).sendKeys(CREDENTIALS);
        driver.waitUntilElementIsVisible(By.id(LOGIN_FORM_SUBMIT_ID));
        driver.findElement(By.id(LOGIN_FORM_SUBMIT_ID)).click();
        driver.waitUntilElementIsVisible(By.id(LOGIN_FORM_PASSWORD_ID));
        driver.findElement(By.id(LOGIN_FORM_PASSWORD_ID)).sendKeys(CREDENTIALS);
        driver.waitUntilElementIsVisible(By.id(LOGIN_FORM_SUBMIT_ID));
        driver.findElement(By.id(LOGIN_FORM_SUBMIT_ID)).click();
        // Sometimes the Matomo page loads slower and to avoid a flicker the timeout needs to be increased.
        driver.waitUntilCondition(ExpectedConditions.visibilityOfElementLocated(By.id(DESCRIPTION_ID)), 15);
        driver.findElement(By.id(DESCRIPTION_ID)).sendKeys("TEST TOKEN");
        driver.waitUntilElementIsVisible(By.cssSelector(BTN_CSS_SELECTOR));
        driver.findElement(By.cssSelector(BTN_CSS_SELECTOR)).click();
        driver.waitUntilElementIsVisible(By.tagName(TAG_NAME_CODE));
        return driver.findElement(By.tagName(TAG_NAME_CODE)).getText();
    }
}


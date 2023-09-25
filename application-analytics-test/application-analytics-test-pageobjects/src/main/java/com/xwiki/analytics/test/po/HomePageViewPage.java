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


import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.View;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.test.ui.XWikiWebDriver;
import org.xwiki.test.ui.po.ViewPage;

public class HomePageViewPage extends ViewPage
{
    public HomePageViewPage()
    {

    }
    public static HomePageViewPage gotoPageHomePage()
    {
        getUtil().gotoPage("Analytics", "WebHome");
        return new HomePageViewPage();
    }
    public static HomePageViewPage gotoAndEdit()
    {
        DocumentReference documentReference = new DocumentReference("xwiki", "Analytics", "WebHome");
        Map<String, String> params =  new HashMap<>();
        params.put("force","1");
        getUtil().gotoPage(documentReference, "edit", params);
        return new HomePageViewPage();
    }
    private static HomePageViewPage clickAddGadget(XWikiWebDriver driver)
    {
        driver.findElement(By.cssSelector(".addgadget")).click();

        return new HomePageViewPage();
    }

    private static void selectMacro(XWikiWebDriver driver, String macroID) {

        WebElement element = driver.findElement(By.cssSelector(String.format("li[data-macroid=\"%s\"]", macroID)));

        // Double-click using JavaScript
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("var event = new MouseEvent('dblclick', { 'bubbles': true }); arguments[0].dispatchEvent(event);", element);

        // Wait for the button to become present in the DOM
        WebDriverWait wait = new WebDriverWait(driver, 10); // waits up to 10 seconds
        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".modal.macro-editor-modal.in.gadget-editor-modal .modal-footer .btn-primary'")));

        button.click();
    }




    public static HomePageViewPage addNewMacro(XWikiWebDriver driver, String macroID)
    {
        clickAddGadget(driver).selectMacro(driver, macroID);
        return new HomePageViewPage();
    }

}

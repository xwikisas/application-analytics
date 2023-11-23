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
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.test.ui.XWikiWebDriver;
import org.openqa.selenium.interactions.Actions;

import org.xwiki.test.ui.po.ViewPage;

public class HomePageViewPage extends ViewPage
{
    
    private static XWikiWebDriver driver;
    public HomePageViewPage()
    {
        this.driver  = getUtil().getDriver();
    }

    public static HomePageViewPage gotoPageHomePage()
    {
        getUtil().gotoPage("Analytics", "WebHome");
        return new HomePageViewPage();
    }

    public static HomePageViewPage gotoAndEdit()
    {
        DocumentReference documentReference = new DocumentReference("xwiki", "Analytics", "WebHome");
        Map<String, String> params = new HashMap<>();
        params.put("force", "1");
        getUtil().gotoPage(documentReference, "edit", params);
        return new HomePageViewPage();
    }

    public static HomePageViewPage addNewMacro(String macroID, String macroName)
    {
        
        clickAddGadget().selectMacro(macroID, macroName);
        return new HomePageViewPage();
    }

    private static HomePageViewPage clickAddGadget()
    {
        driver.findElement(By.cssSelector(".addgadget")).click();
        return new HomePageViewPage();
    }

    private static void waitAndClick(String css)
    {
        driver.waitUntilElementIsEnabled(driver.findElement(By.cssSelector(css)));
        driver.findElement(By.cssSelector(css)).click();
    }
    private static void selectMacro(String macroID, String macroName)
    {
        // Will bring into view the macro that I want to use.
        driver.findElement(By.cssSelector(".macro-textFilter")).sendKeys(macroName);
        waitAndClick(String.format("li[data-macroid=\"%s\"]", macroID));
        // Will wait until the select button becomes enabled and will click on it.
        waitAndClick(".modal.macro-selector-modal.gadget-selector-modal.in .modal-footer .btn-primary");
        // Will wait until the submit button becomes enabled and will click on it.
        waitAndClick(".modal.macro-editor-modal.in .modal-footer .btn-primary");
    }

    public static int noOfGadgets()
    {
       return driver.findElements(By.className("gadget")).size();
    }
    public static HomePageViewPage saveDashboard()
    {
        waitAndClick(".bottombuttons.sticky-buttons .btn-primary");
        return new HomePageViewPage();
    }
    public static HomePageViewPage removeLastMacro()
    {

        WebElement lastGadget = driver.findElement(By.cssSelector(".gadget:last-of-type"));
        // Move the cursor to be on top of the macro to reveal the remove button.
        new Actions(driver.getWrappedDriver()).moveToElement(lastGadget).perform();
        // Click on the remove button when it becomes available.
        waitAndClick(".gadget:last-of-type .remove");
        // Click on the confirm button to remove the macro.
        waitAndClick(".xdialog-box.xdialog-box-confirmation .xdialog-content .buttonwrapper");
        return new HomePageViewPage();
    }

    public static String getMacroDescription(int id)
    {
        List<WebElement> elements = driver.findElements(By.cssSelector(".analytics-description"));
        return elements.get(id).getAttribute("title");
    }

}

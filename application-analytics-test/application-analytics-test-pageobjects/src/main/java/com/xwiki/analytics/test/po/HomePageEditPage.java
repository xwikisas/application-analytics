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
import org.xwiki.test.ui.XWikiWebDriver;
import org.xwiki.test.ui.po.editor.EditPage;

public class HomePageEditPage extends EditPage
{
    private final XWikiWebDriver driver;

    public HomePageEditPage()
    {
        driver = getUtil().getDriver();
    }

    /**
     * Adds a new macro to the homepage of the application.
     * @param macroID id of the macro
     * @param macroName name of the macro
     * @return
     */

    public HomePageEditPage addNewMacro(String macroID, String macroName)
    {

        this.clickAddGadget().selectMacro(macroID, macroName);
        return this;
    }

    /**
     * Removes the last macro that is on the page.
     * @return
     */

    public HomePageEditPage removeLastMacro()
    {

        WebElement lastGadget = driver.findElement(By.cssSelector(".gadget:last-of-type"));
        // Move the cursor to be on top of the macro to reveal the remove button.
        new Actions(driver.getWrappedDriver()).moveToElement(lastGadget).perform();
        // Click on the remove button when it becomes available.
        waitAndClick(".gadget:last-of-type .remove");
        // Click on the confirm button to remove the macro.
        waitAndClick(".xdialog-box.xdialog-box-confirmation .xdialog-content .buttonwrapper");
        return this;
    }

    public HomePageViewPage saveDashboard()
    {

        EditPage editPage = new EditPage();
        editPage.clickSaveAndView();
        return new HomePageViewPage();
    }

    private HomePageEditPage clickAddGadget()
    {
        driver.findElement(By.cssSelector(".addgadget")).click();
        return this;
    }

    private void waitAndClick(String css)
    {
        driver.waitUntilElementIsEnabled(driver.findElement(By.cssSelector(css)));
        driver.findElement(By.cssSelector(css)).click();
    }

    private void selectMacro(String macroID, String macroName)
    {
        // Will bring into view the macro that I want to use.
        driver.findElement(By.cssSelector(".macro-textFilter")).sendKeys(macroName);
        waitAndClick(String.format("li[data-macroid=\"%s\"]", macroID));
        // Will wait until the select button becomes enabled and will click on it.
        waitAndClick(".modal.macro-selector-modal.gadget-selector-modal.in .modal-footer .btn-primary");
        // Will wait until the submit button becomes enabled and will click on it.
        waitAndClick(".modal.macro-editor-modal.in .modal-footer .btn-primary");
    }
}

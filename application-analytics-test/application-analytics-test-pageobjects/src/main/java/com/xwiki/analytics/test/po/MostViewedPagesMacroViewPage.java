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

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.xwiki.test.ui.XWikiWebDriver;
import org.xwiki.test.ui.po.BaseModal;
import org.xwiki.test.ui.po.ViewPage;

/**
 * View for the MostViewedPages macro that lets us interact with the page of the macro.
 */
public class MostViewedPagesMacroViewPage extends ViewPage
{
    private static final String ROW_EVOLUTION_BUTTON_SELECTOR = ".analyticsRowEvolution";

    private final XWikiWebDriver driver;

    public MostViewedPagesMacroViewPage()
    {
        driver = getUtil().getDriver();
    }

    public MostViewedPagesMacroViewPage gotoPage()
    {
        List<String> spaces = Arrays.asList("Analytics", "Code", "Macros");
        getUtil().gotoPage(spaces, "MostViewedPages", "view", "");
        return this;
    }

    /**
     * Opens the Row Evolution modal and waits until it is fully visible before returning it.
     * @return the modal
     */
    public BaseModal openRowEvolutionModal()
    {
        BaseModal baseModal = new BaseModal(By.cssSelector(".modal-dialog.modal-lg"));
        driver.waitUntilElementIsVisible(By.cssSelector(ROW_EVOLUTION_BUTTON_SELECTOR));
        driver.findElement(By.cssSelector(ROW_EVOLUTION_BUTTON_SELECTOR)).click();
        return baseModal;
    }

    /***
     * This method first waits for the visibility of an information button, identified by a specific CSS selector.
     * Once the information button is visible it selects the tile attribute and returns it.
     * @return THe description of a macro.
     */
    public String getMacroDescription()
    {
        driver.waitUntilElementIsVisible(By.cssSelector(".xcontent h2 div a"));
        return driver.findElement(By.cssSelector(".xcontent h2 div a")).getAttribute("title");
    }
}

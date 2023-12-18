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
 * View for the MostViewedPages macro that let's us interact with the page.
 */
public class MostViewedMacroViewPages extends ViewPage
{
    private final XWikiWebDriver driver;

    private static final String ROW_EVOLUTION_BUTTON_SELECTOR = ".analyticsRowEvolution";

    public MostViewedMacroViewPages()
    {
        driver = getUtil().getDriver();
    }

    public MostViewedMacroViewPages gotoPage()
    {
        List<String> spaces = Arrays.asList("Analytics", "Code", "Macros");
        getUtil().gotoPage(spaces, "MostViewedPages", "view", "");
        return this;
    }

    public BaseModal openRowEvolutionModal()
    {
        BaseModal baseModal = new BaseModal(By.cssSelector(".modal-dialog.modal-lg"));
        driver.waitUntilElementIsVisible(By.cssSelector(ROW_EVOLUTION_BUTTON_SELECTOR));
        driver.findElement(By.cssSelector(ROW_EVOLUTION_BUTTON_SELECTOR)).click();
        return baseModal;
    }

    public String getMacroDescription()
    {
        driver.waitUntilElementIsVisible(By.cssSelector(".xcontent h2 div a"));
        return driver.findElement(By.cssSelector(".xcontent h2 div a")).getAttribute("title");
    }

}

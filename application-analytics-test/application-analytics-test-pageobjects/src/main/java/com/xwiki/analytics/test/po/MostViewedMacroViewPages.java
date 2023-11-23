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

import java.util.List;
import java.util.Arrays;

import org.openqa.selenium.By;
import org.xwiki.test.ui.XWikiWebDriver;
import org.xwiki.test.ui.po.ViewPage;

public class MostViewedMacroViewPages extends ViewPage
{

    private  static XWikiWebDriver driver;
    private static final String ROW_EVOLUTION_BUTTON_SELECTOR = ".analyticsRowEvolution";
    private static final String ROW_EVOLUTION_MODAL_SELECTOR = ".modal.fade.analyticsRowEvolutionModal";
    public MostViewedMacroViewPages()
    {
        driver =  getUtil().getDriver();
    }

    public static MostViewedMacroViewPages gotoPage()
    {
        List<String> spaces = Arrays.asList("Analytics", "Code", "Macros");
        getUtil().gotoPage(spaces, "MostViewedPages", "view", "");
        return new MostViewedMacroViewPages();
    }

    public static void openRowEvolutionModal()
    {
        driver.waitUntilElementIsVisible(By.cssSelector(ROW_EVOLUTION_BUTTON_SELECTOR));
        driver.findElement(By.cssSelector(ROW_EVOLUTION_BUTTON_SELECTOR)).click();
    }

    public static boolean isModalDisplayed()
    {
        driver.waitUntilElementIsVisible(By.cssSelector(ROW_EVOLUTION_MODAL_SELECTOR));
        if(driver.findElement(By.cssSelector(ROW_EVOLUTION_MODAL_SELECTOR)).isDisplayed())
            return true;
        return false;
    }
    public static void closeModal()
    {
        driver.findElement(By.cssSelector(".btn.btn-default")).click();
    }
}

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
import org.xwiki.test.ui.po.BaseElement;

/**
 * View for the MostViewedPages macro that lets us interact with the macro.
 */
public class MostViewedPagesElement extends BaseElement
{
    static final private String MODAL_CSS = ".modal-dialog.modal-lg";

    private WebElement container;

    public MostViewedPagesElement()
    {
    }

    /***
     * This method first waits for the visibility of an information button, identified by a specific CSS selector.
     * Once the information button is visible it selects the tile attribute and returns it.
     * @return the description of a macro.
     */
    public String getMacroDescription()
    {
        return container.findElement(By.cssSelector(".analyticsDescription")).getAttribute("title");
    }

    public RowEvolutionModal openModal()
    {
        RowEvolutionModal modal = new RowEvolutionModal(By.cssSelector(MODAL_CSS));
        modal.openModal();
        return modal;
    }

    public void waitUntilReady(String id)
    {
        getDriver().waitUntilElementIsVisible(By.id(id));
        container = getDriver().findElement(By.id(id));
    }
}

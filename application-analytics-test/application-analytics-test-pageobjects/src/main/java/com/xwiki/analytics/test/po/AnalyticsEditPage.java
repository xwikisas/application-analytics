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
import java.util.Map;

import org.openqa.selenium.By;
import org.xwiki.ckeditor.test.po.MacroDialogSelectModal;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.test.ui.po.editor.EditPage;

/**
 * Responsible for interacting with the main page of the application when the page is in edit mode.
 */
public class AnalyticsEditPage extends EditPage
{
    public AnalyticsEditPage()
    {
    }

    /**
     * Adds a new macro to the homepage of the application.
     *
     * @param macroName name of the macro
     * @return
     */
    public AnalyticsEditPage addNewMacro(String macroName)
    {

        this.clickAddGadget().selectMacro(macroName);
        return this;
    }

    public static AnalyticsEditPage gotoPage()
    {
        DocumentReference documentReference = new DocumentReference("xwiki", "Analytics", "WebHome");
        Map<String, String> params = new HashMap<>();
        params.put("force", "1");
        getUtil().gotoPage(documentReference, "edit", params);
        return new AnalyticsEditPage();
    }

    public AnalyticsViewPage saveDashboard()
    {
        this.clickSaveAndView();
        return new AnalyticsViewPage();
    }

    private AnalyticsEditPage clickAddGadget()
    {
        getDriver().findElement(By.cssSelector(".addgadget")).click();
        return this;
    }

    private void selectMacro(String macroName)
    {
        MacroDialogSelectModal macroDialogSelectModal = new MacroDialogSelectModal();
        // Will bring into view the macro that I want to use.
        getDriver().findElement(By.cssSelector(".macro-textFilter")).sendKeys(macroName);
        macroDialogSelectModal.filterByText(macroName, 1);
        macroDialogSelectModal.getFirstMacro();
        macroDialogSelectModal.clickSelect();
        // Right now the MacroDialogSelectModal doesn't have a method to press submit.
        String css = ".modal.macro-editor-modal.in .modal-footer .btn-primary";
        getDriver().waitUntilElementIsEnabled(getUtil().getDriver().findElement(By.cssSelector(css)));
        getDriver().findElement(By.cssSelector(css)).click();
    }
}

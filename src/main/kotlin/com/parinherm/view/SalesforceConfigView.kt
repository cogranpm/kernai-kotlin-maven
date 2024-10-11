package com.parinherm.view

import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.SalesforceConfig
import com.parinherm.form.Form
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.form.makeHeaderText
import com.parinherm.image.ImageUtils
import com.parinherm.server.DefaultViewDefinitions
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Text


class SalesforceConfigView(val parent: Composite, comparator: BeansViewerComparator)  : View <SalesforceConfig> {
    override val form: Form<SalesforceConfig> = Form(parent, DefaultViewDefinitions.loadView(ViewDefConstants.salesforceConfigViewId), comparator)

    /*
    val editToolbar = form.toolbar
    val testButton = Button(editToolbar, SWT.PUSH)
    val createLeadButton = Button(editToolbar, SWT.PUSH)
    val commandOutput = Text(form.headerSection, SWT.READ_ONLY)
    val commandInput = Text(form.headerSection, SWT.SINGLE or SWT.BORDER)
    val commandHelp = Text(form.headerSection, SWT.READ_ONLY)
     */

    init {
        //makeHeaderText(form.headerSection, "Revision will duplicate Quiz Run with incorrectly answered questions only.")

        /*
        testButton.image = ImageUtils.getImage("bookmark-new")
        testButton.text = "Test"
        testButton.toolTipText = "Test"

        createLeadButton.image = ImageUtils.getImage("bookmark-new")
        createLeadButton.text = "Create Lead"
        createLeadButton.toolTipText = "Create lead"

        commandHelp.text = "paste in specific rest command here, for example: /services/data/v32.0/sobjects/Account/describe"

        GridDataFactory.fillDefaults().grab(false, false).applyTo(testButton)
        GridDataFactory.fillDefaults().grab(false, false).applyTo(createLeadButton)
        GridDataFactory.fillDefaults().grab(true, false).applyTo(commandOutput)
        GridDataFactory.fillDefaults().grab(true, false).applyTo(commandHelp)
        GridDataFactory.fillDefaults().grab(true, false).applyTo(commandInput)

         */
    }

}
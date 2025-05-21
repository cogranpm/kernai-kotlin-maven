package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.ViewDefinition
import com.parinherm.form.Form
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.form.makeHeaderText
import com.parinherm.image.ImageUtils
import com.parinherm.server.DefaultViewDefinitions
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.layout.RowData
import org.eclipse.swt.layout.RowLayout
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Text

class ViewDefinitionView(val parent: Composite, comparator: BeansViewerComparator)
    : View <ViewDefinition> {

        override val form: Form<ViewDefinition> = Form(parent,
            DefaultViewDefinitions.loadView(ViewDefConstants.viewDefinitionViewId),
            comparator
        )


    val editToolbar = form.toolbar
    val toolbar = Composite(editToolbar, SWT.BORDER)
    val btnMake = Button(toolbar, SWT.PUSH)
    val btnScaffold = Button(toolbar, SWT.PUSH)
    val btnScaffoldAdvanced = Button(toolbar, SWT.PUSH)
    val txtProgress = Text(toolbar, SWT.BORDER or SWT.READ_ONLY)
    val commandOutput = Text(form.headerSection, SWT.READ_ONLY or SWT.MULTI or SWT.V_SCROLL)
    val toolbarHelp = Composite(editToolbar, SWT.BORDER)
    val lblHelp = Label(toolbarHelp, SWT.BORDER)
    val txtHelp = Text(toolbarHelp, SWT.BORDER or SWT.READ_ONLY or SWT.MULTI)

    init {
        makeHeaderText(form.headerSection, "Run Scaffold to scaffold source code")
        GridDataFactory.fillDefaults().grab(true, true).applyTo(commandOutput)
        toolbar.layout = GridLayout(4, false)
        btnMake.text = "Make"
        btnMake.toolTipText = "write the script file to user directory"
        btnMake.image = ImageUtils.getImage("image-x-generic")
        GridDataFactory.fillDefaults().grab(false, false).applyTo(btnMake)

        btnScaffold.text = "Scaffold"
        btnScaffold.toolTipText = "Run Scaffold script"
        btnScaffold.image = ImageUtils.getImage("image-x-generic")
        GridDataFactory.fillDefaults().grab(false, false).applyTo(btnScaffold)

        btnScaffoldAdvanced.text = "Scaffold Advanced"
        btnScaffoldAdvanced.toolTipText = "Run Advanced Scaffold script"
        btnScaffoldAdvanced.image = ImageUtils.getImage("image-x-generic")
        GridDataFactory.fillDefaults().grab(false, false).applyTo(btnScaffoldAdvanced)

        GridDataFactory.fillDefaults().grab(true, false).applyTo(txtProgress)


        GridDataFactory.fillDefaults().grab(true, false).applyTo(toolbar)
        GridDataFactory.fillDefaults().grab(true, true).applyTo(toolbarHelp)
        toolbarHelp.layout = GridLayout(1, false)
        GridDataFactory.fillDefaults().grab(true, false).applyTo(lblHelp)
        lblHelp.text = "Config Options"
        txtHelp.text = """permissionName, 
            |menuTitle, 
            |viewStyle(|report|table), 
            | customTableName,
            | customPrimaryKey,
            |sortBy: [{ name: CreatedOn, dir: desc}, {name: OrderDate, dir: desc}]""".trimMargin();
        GridDataFactory.fillDefaults().grab(true, true).applyTo(txtHelp)
    }

}
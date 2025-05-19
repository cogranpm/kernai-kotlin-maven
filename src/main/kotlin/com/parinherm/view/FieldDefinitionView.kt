package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.FieldDefinition
import com.parinherm.form.Form
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.form.makeHeaderText
import com.parinherm.server.DefaultViewDefinitions
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Text

class FieldDefinitionView(val parent: Composite, comparator: BeansViewerComparator)
    : View <FieldDefinition> {

        override val form: Form<FieldDefinition> = Form(parent,
            DefaultViewDefinitions.loadView(ViewDefConstants.fieldDefinitionViewId),
            comparator
        )
// and field.configMap["advancedLookup"] == "true"

    val editToolbar = form.toolbar
    val toolbarHelp = Composite(editToolbar, SWT.BORDER)
    val lblHelp = Label(toolbarHelp, SWT.BORDER)
    val txtHelp = Text(toolbarHelp, SWT.BORDER or SWT.READ_ONLY or SWT.MULTI)
    init {

        GridDataFactory.fillDefaults().grab(true, true).applyTo(toolbarHelp)
        toolbarHelp.layout = GridLayout(1, false)
        GridDataFactory.fillDefaults().grab(true, false).applyTo(lblHelp)
        lblHelp.text = "Config Options"
        txtHelp.text = """advancedLookup (true|false) | showInList (true|false)""".trimMargin();
        GridDataFactory.fillDefaults().grab(true, true).applyTo(txtHelp)
    }

}
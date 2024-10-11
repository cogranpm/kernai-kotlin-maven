package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Snippet
import com.parinherm.form.Form
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.form.makeHeaderText
import com.parinherm.image.ImageUtils
import com.parinherm.server.DefaultViewDefinitions
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.jface.layout.GridLayoutFactory
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.RowLayout
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Text


class SnippetView (val parent: Composite, comparator: BeansViewerComparator) : View <Snippet>{

    // this member has all of the widgets
    // it's a common object favour composition over inheritance
    override val form: Form<Snippet> = Form(
        parent,
        DefaultViewDefinitions.loadView(ViewDefConstants.techSnippetsViewId),
        comparator)

    val editToolbar = form.toolbar
    val runScriptButton = Button(editToolbar, SWT.PUSH)

    init {
        makeHeaderText(form.headerSection, "Enter snippets of code in your programming language. Execute the code using the run button. Only kotlin or javascript can be executed at this time.")
        runScriptButton.image = ImageUtils.getImage("application-x-executable")
        runScriptButton.text = "Execute"
        runScriptButton.toolTipText = "run script (kotlin or javascript only)"
        GridDataFactory.fillDefaults().grab(false, false).applyTo(runScriptButton)
    }

}
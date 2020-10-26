package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Snippet
import com.parinherm.form.Form
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.RowLayout
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite


class SnippetView (val parent: Composite, comparator: BeansViewerComparator) : View <Snippet>{
    val formDef: Map<String, Any> =
            ApplicationData.getView(
                    ApplicationData.ViewDefConstants.techSnippetsViewId,
                    ApplicationData.viewDefinitions
            )

    // this member has all of the widgets
    // it's a common object favour composition over inheritance
    override val form: Form<Snippet> = Form(parent, formDef, comparator)
    val editContainer = form.formsContainer.editContainer

    /***** test buttons *********/
    val toolbar = Composite(editContainer, SWT.BORDER)
    val testScriptButton = Button(toolbar, SWT.PUSH)
    val graalScriptButton = Button(toolbar, SWT.PUSH)

    init {

        toolbar.layout = RowLayout()
        testScriptButton.text = "Run"
        graalScriptButton.text = "Graal JS Test"
   }

}
package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Notebook
import com.parinherm.form.Form
import org.eclipse.swt.widgets.Composite

class NotebookView (val parent: Composite, comparator: BeansViewerComparator) : View<Notebook>  {
    private val formDef: Map<String, Any> =
        ApplicationData.getView(
            ApplicationData.ViewDefConstants.notebookViewId,
            ApplicationData.viewDefinitions
        )

    // this member has all of the widgets
    // it's a common object favour composition over inheritance
    override val form: Form<Notebook> = Form(parent, formDef, comparator)

}
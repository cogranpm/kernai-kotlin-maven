package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.NoteDetail
import com.parinherm.form.Form
import org.eclipse.swt.widgets.Composite

class NoteDetailView (val parent: Composite, comparator: BeansViewerComparator) : View<NoteDetail> {
    private val formDef: Map<String, Any> =
        ApplicationData.getView(
            ApplicationData.ViewDefConstants.noteDetailViewId,
            ApplicationData.viewDefinitions
        )

    // this member has all of the widgets
    // it's a common object favour composition over inheritance
    override val form: Form<NoteDetail> = Form(parent, formDef, comparator)

}
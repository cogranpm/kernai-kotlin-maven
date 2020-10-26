package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.NoteHeader
import com.parinherm.form.Form
import org.eclipse.swt.widgets.Composite

class NoteHeaderView (val parent: Composite, comparator: BeansViewerComparator) : View<NoteHeader>{
    private val formDef: Map<String, Any> =
        ApplicationData.getView(
            ApplicationData.ViewDefConstants.noteheaderViewId,
            ApplicationData.viewDefinitions
        )

    // this member has all of the widgets
    // it's a common object favour composition over inheritance
    override val form: Form<NoteHeader> = Form(parent, formDef, comparator)

}
package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.NoteHeader
import com.parinherm.form.Form
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.server.DefaultViewDefinitions
import com.parinherm.view.filter.NoteHeaderViewFilter
import org.eclipse.swt.widgets.Composite

class NoteHeaderView (val parent: Composite, comparator: BeansViewerComparator) : View<NoteHeader>{

    // this member has all of the widgets
    // it's a common object favour composition over inheritance
    override val form: Form<NoteHeader> = Form(parent,
        DefaultViewDefinitions.loadView(ViewDefConstants.noteheaderViewId),
        comparator,
        NoteHeaderViewFilter()
    )

}
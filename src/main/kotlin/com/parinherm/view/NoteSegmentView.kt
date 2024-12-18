package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.NoteSegment
import com.parinherm.form.Form
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.server.DefaultViewDefinitions
import org.eclipse.swt.widgets.Composite

class NoteSegmentView(val parent: Composite, comparator: BeansViewerComparator)
    : View <NoteSegment> {

        // this member has all of the widgets
        // it's a common object favour composition over inheritance
        override val form: Form<NoteSegment> = Form(parent,
            DefaultViewDefinitions.loadView(ViewDefConstants.noteSegmentViewId),
            comparator
        )

}
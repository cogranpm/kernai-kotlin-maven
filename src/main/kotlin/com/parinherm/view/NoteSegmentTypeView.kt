package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.NoteSegmentType
import com.parinherm.form.Form
import org.eclipse.swt.widgets.Composite

class NoteSegmentTypeView(val parent: Composite, comparator: BeansViewerComparator)
    : View <NoteSegmentType> {

        // this member has all of the widgets
        // it's a common object favour composition over inheritance
        override val form: Form<NoteSegmentType> = Form(parent,
            ApplicationData.getView(ApplicationData.ViewDefConstants.noteSegmentTypeViewId),
            comparator
        )

}
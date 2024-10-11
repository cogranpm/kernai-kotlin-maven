package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Subject
import com.parinherm.form.Form
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.server.DefaultViewDefinitions
import org.eclipse.swt.widgets.Composite

class SubjectView(val parent: Composite, comparator: BeansViewerComparator)
    : View <Subject> {

        // this member has all of the widgets
        // it's a common object favour composition over inheritance
        override val form: Form<Subject> = Form(parent,
            DefaultViewDefinitions.loadView(ViewDefConstants.subjectViewId),
            comparator
        )

}
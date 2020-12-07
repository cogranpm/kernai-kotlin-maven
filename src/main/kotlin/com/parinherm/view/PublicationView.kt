package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Publication
import com.parinherm.form.Form
import org.eclipse.swt.widgets.Composite

class PublicationView(val parent: Composite, comparator: BeansViewerComparator)
    : View <Publication> {

        // this member has all of the widgets
        // it's a common object favour composition over inheritance
        override val form: Form<Publication> = Form(parent,
            ApplicationData.getView(ApplicationData.ViewDefConstants.publicationViewId),
            comparator
        )

}
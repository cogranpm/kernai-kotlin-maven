package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Shelf
import com.parinherm.form.Form
import org.eclipse.swt.widgets.Composite

class ShelfView(val parent: Composite, comparator: BeansViewerComparator)
    : View <Shelf> {

        // this member has all of the widgets
        // it's a common object favour composition over inheritance
        override val form: Form<Shelf> = Form(parent,
            ApplicationData.getView(ApplicationData.ViewDefConstants.shelfViewId),
            comparator
        )

}
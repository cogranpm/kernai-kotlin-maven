package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Person
import com.parinherm.form.Form
import org.eclipse.swt.widgets.Composite

class PersonView(val parent: Composite, comparator: BeansViewerComparator) : View <Person> {

    // this member has all of the widgets
    // it's a common object favour composition over inheritance
    override val form: Form<Person> = Form(parent, ApplicationData.getView(ApplicationData.ViewDefConstants.personViewId), comparator)

    init {
    }


}
package com.parinherm.view

import com.parinherm.entity.Person
import com.parinherm.form.Form
import com.parinherm.viewmodel.PersonViewModel
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Widget

class PersonView (val parent: Composite, val viewDefinition: Map<String, Any>) : View {
    val viewModel = PersonViewModel(this)

    // this member has all of the widgets
    // it's a common object favour composition over inheritance
    override val form: Form <Person> = Form(parent, viewDefinition, Person.Comparator())

    override fun refresh() {
        // when the lists should refresh themselves
        // say if a child tab adds a record
    }

    override fun getSaveButton(): Button{
        return Button(null, SWT.NONE)
    }
}
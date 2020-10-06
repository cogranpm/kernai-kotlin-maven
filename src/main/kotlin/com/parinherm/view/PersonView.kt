package com.parinherm.view

import com.parinherm.form.Form
import com.parinherm.viewmodel.PersonViewModel
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Widget

class PersonView (val parent: Composite, val viewDefinition: Map<String, Any>) : View {
    val viewModel = PersonViewModel(this)
    override val form: Form = Form(parent, viewDefinition)
    override fun refresh() {
    }

    override fun getSaveButton(): Button{
        return Button(null, SWT.NONE)
    }
}
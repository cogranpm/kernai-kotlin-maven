package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Person
import com.parinherm.form.Form
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite

class PersonView(val parent: Composite, comparator: BeansViewerComparator) : View {

    val formDef: Map<String, Any> =
        ApplicationData.getView(
            ApplicationData.ViewDef.beansBindingTestViewId,
            ApplicationData.viewDefinitions
        )

    // this member has all of the widgets
    // it's a common object favour composition over inheritance
    override val form: Form<Person> = Form(parent, formDef, comparator)

    init {
        refresh()
    }

    override fun refresh() {
        // when the lists should refresh themselves
        // say if a child tab adds a record
        //val data = PersonMapper.getAll(mapOf())
        //viewModel.setData(data)
    }

    override fun getSaveButton(): Button {
        return Button(null, SWT.NONE)
    }
}
package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Person
import com.parinherm.form.Form
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite

class PersonView(val parent: Composite, comparator: BeansViewerComparator) : View <Person> {

    val formDef: Map<String, Any> =
        ApplicationData.getView(
            ApplicationData.ViewDef.personViewId,
            ApplicationData.viewDefinitions
        )

    // this member has all of the widgets
    // it's a common object favour composition over inheritance
    override val form: Form<Person> = Form(parent, formDef, comparator)

    init {
    }


}
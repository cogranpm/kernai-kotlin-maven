package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Lookup
import com.parinherm.entity.Recipe
import com.parinherm.form.Form
import org.eclipse.swt.widgets.Composite

class LookupView(val parent: Composite, comparator: BeansViewerComparator) : View <Lookup> {

    override val form: Form<Lookup> = Form(parent, ApplicationData.getView(ApplicationData.ViewDefConstants.lookupViewId), comparator)
}
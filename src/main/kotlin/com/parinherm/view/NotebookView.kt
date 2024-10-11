package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Notebook
import com.parinherm.form.Form
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.server.DefaultViewDefinitions
import com.parinherm.view.filter.NotebookViewFilter
import org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter
import org.eclipse.swt.widgets.Composite

class NotebookView (val parent: Composite, comparator: BeansViewerComparator) : View<Notebook>  {
   // this member has all of the widgets
    // it's a common object favour composition over inheritance
    override val form: Form<Notebook> = Form(parent,
       DefaultViewDefinitions.loadView(ViewDefConstants.notebookViewId),
       comparator, NotebookViewFilter())


}
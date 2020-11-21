package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Notebook
import com.parinherm.form.Form
import com.parinherm.view.filter.NotebookBaseViewFilter
import org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter
import org.eclipse.swt.widgets.Composite

class NotebookView (val parent: Composite, comparator: BeansViewerComparator) : View<Notebook>  {
   // this member has all of the widgets
    // it's a common object favour composition over inheritance
    override val form: Form<Notebook> = Form(parent,
       ApplicationData.getView(ApplicationData.ViewDefConstants.notebookViewId),
       comparator, NotebookBaseViewFilter())

    init {
        val searchField = form.listFilters["name"]
        if(form.searchButton != null) {
            form?.searchButton?.addSelectionListener(widgetSelectedAdapter
            {
                if (searchField != null) {
                    form.filter?.searchText = "${searchField.text}"
                    form.listView.refresh()
                }
            })
        }

    }


}
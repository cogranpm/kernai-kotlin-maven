package com.parinherm.view.filter

import com.parinherm.entity.Notebook
import org.eclipse.jface.viewers.Viewer
import org.eclipse.jface.viewers.ViewerFilter
import org.eclipse.swt.widgets.Text


class NotebookViewFilter( ) : BaseViewFilter() {

    override fun select(viewer: Viewer?, parentElement: Any?, element: Any?): Boolean {
        if (element != null && element is Notebook && searchFields != null)
        {
            /* search on the name field, provide name of text widget and a function reference to the entity property we are searching on */
            return searchOnStringField(searchFields,"name", element::name)
        }
        return false
    }
}
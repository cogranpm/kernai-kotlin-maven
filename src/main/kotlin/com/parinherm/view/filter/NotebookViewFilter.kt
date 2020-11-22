package com.parinherm.view.filter

import com.parinherm.entity.Notebook
import org.eclipse.jface.viewers.Viewer
import org.eclipse.jface.viewers.ViewerFilter
import org.eclipse.swt.widgets.Text


class NotebookViewFilter( ) : BaseViewFilter() {

    override var searchFields: Map<String, Text>? = null

    override fun select(viewer: Viewer?, parentElement: Any?, element: Any?): Boolean {
        if (element != null && element is Notebook && searchFields != null)
        {
            val txtName = searchFields!!["name"]
            val nameValue = txtName?.text
            val regex = ".*${nameValue?.toLowerCase()}.*".toRegex()
            return regex.containsMatchIn(element.name.toLowerCase())

        }
        return false
    }
}
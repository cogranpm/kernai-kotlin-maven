package com.parinherm.view.filter

import com.parinherm.entity.Notebook
import org.eclipse.jface.viewers.Viewer
import org.eclipse.jface.viewers.ViewerFilter

class NotebookViewFilter : ViewerFilter() {

    var searchText = ""

    override fun select(viewer: Viewer?, parentElement: Any?, element: Any?): Boolean {
        if (searchText.isNullOrBlank()) return true
        val regex = ".*${searchText.toLowerCase()}.*".toRegex()
        if (element != null && element is Notebook)
        {
            if (regex.containsMatchIn(element.name.toLowerCase())){
                return true
            }
            else {
                return false
            }

        }
        return false
    }
}
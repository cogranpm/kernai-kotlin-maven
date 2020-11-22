package com.parinherm.view.filter

import org.eclipse.jface.viewers.Viewer
import org.eclipse.jface.viewers.ViewerFilter
import org.eclipse.swt.widgets.Text

abstract class BaseViewFilter : ViewerFilter() {
    abstract var searchFields: Map<String, Text>?

    protected fun searchOnStringField(fieldName: String, fieldAccessor: () -> String): Boolean {
        if (searchFields != null)
        {
            val txtField = searchFields!![fieldName]
            val fieldValue = txtField?.text
            val regex = ".*${fieldValue?.toLowerCase()}.*".toRegex()
            return regex.containsMatchIn(fieldAccessor().toLowerCase())
        }
        return false
    }
}
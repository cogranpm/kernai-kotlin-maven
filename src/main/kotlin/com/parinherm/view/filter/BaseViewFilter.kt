package com.parinherm.view.filter

import com.parinherm.entity.NoteHeader
import org.eclipse.jface.viewers.Viewer
import org.eclipse.jface.viewers.ViewerFilter
import org.eclipse.swt.widgets.Text

abstract class BaseViewFilter() : ViewerFilter() {

    var searchFields: Map<String, Text>? = null

    companion object {

        fun searchOnStringField(searchFields: Map<String, Text>?,
                                fieldName: String,
                                fieldAccessor: () -> String): Boolean {
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
}
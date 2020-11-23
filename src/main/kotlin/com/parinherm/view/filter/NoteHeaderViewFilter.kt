package com.parinherm.view.filter

import com.parinherm.entity.NoteHeader
import org.eclipse.jface.viewers.Viewer

class NoteHeaderViewFilter : BaseViewFilter() {

    override fun select(viewer: Viewer?, parentElement: Any?, element: Any?): Boolean {
        if (element != null && element is NoteHeader && searchFields != null)
        {
            return searchOnStringField(searchFields,"name", element::name)
        }
        return false
    }
}

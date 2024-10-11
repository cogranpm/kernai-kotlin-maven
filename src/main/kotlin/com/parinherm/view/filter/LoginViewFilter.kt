package com.parinherm.view.filter

import com.parinherm.entity.Login
import org.eclipse.jface.viewers.Viewer

class LoginViewFilter : BaseViewFilter() {

    override fun select(viewer: Viewer?, parentElement: Any?, element: Any?): Boolean {
        if (element != null && element is Login && searchFields != null)
        {
            /* search on the name field */
            return searchOnStringField(searchFields,"name", element::name)
        }
        return false
    }
}
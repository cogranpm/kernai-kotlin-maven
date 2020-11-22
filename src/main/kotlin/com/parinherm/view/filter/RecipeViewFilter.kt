package com.parinherm.view.filter

import com.parinherm.entity.Recipe
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.widgets.Text

class RecipeViewFilter : BaseViewFilter() {
    override var searchFields: Map<String, Text>? = null

    override fun select(viewer: Viewer?, parentElement: Any?, element: Any?): Boolean {
        if (element != null && element is Recipe && searchFields != null)
        {
            /* search on the name field */
            return searchOnStringField("name", element::name)
        }
        return false
    }
}
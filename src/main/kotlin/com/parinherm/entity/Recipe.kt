package com.parinherm.entity

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import org.eclipse.jface.viewers.Viewer
import kotlin.properties.Delegates

class Recipe(
    override var id: Long = 0,
    name: String,
    method: String,
    category: String
) : ModelObject(), IBeanDataEntity {


    var name: String by Delegates.observable(name, observer)
    var method: String by Delegates.observable(method, observer)
    var category: String by Delegates.observable(method, observer)

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> name
            1 -> method
            2 -> category
            else -> ""
        }
    }



    override fun toString(): String {
        return "Recipe(id=$id, name=$name, method=$method)"
    }

    companion object Factory {
        fun make(): Recipe {
            return Recipe(0, "", "", "")
        }
    }
}
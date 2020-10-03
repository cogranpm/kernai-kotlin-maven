package com.parinherm.entity

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import org.eclipse.jface.viewers.Viewer
import kotlin.properties.Delegates

class Recipe (override var id: Long = 0, name: String, method: String) : ModelObject(), IBeanDataEntity{


    var name: String by Delegates.observable(name, observer)
    var method: String by Delegates.observable(method, observer)

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> name
            1 -> method
            else -> ""
        }
    }

    class Comparator : BeansViewerComparator(), IViewerComparator {

        val name_index = 0
        val method_index = 1

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Recipe
            val entity2 = e2 as Recipe
            val rc = when(propertyIndex){
                name_index -> entity1.name.compareTo(entity2.name)
                method_index -> entity1.method.compareTo(entity2.method)
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }


    override fun toString(): String {
        return "Recipe(id=$id, name=$name, method=$method)"
    }

    companion object Factory {
        fun make() : Recipe{
            return Recipe(0, "",  "")
        }
    }
}
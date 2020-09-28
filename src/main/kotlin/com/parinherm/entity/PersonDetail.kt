package com.parinherm.entity

import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import org.eclipse.jface.viewers.Viewer
import kotlin.properties.Delegates

class PersonDetail (override var id: Long = 0, nickname: String, var beanTestId: Long) : ModelObject(), IBeanDataEntity {

    var nickname: String by Delegates.observable(nickname, observer)

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> this.nickname
            else -> ""
        }
    }

    class Comparator : BeansViewerComparator(), IViewerComparator {

        val nickname_index = 0


        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as PersonDetail
            val entity2 = e2 as PersonDetail
            val rc = when(propertyIndex){
                nickname_index -> entity1.nickname.compareTo(entity2.nickname)
               else -> 0
            }
            return flipSortDirection(rc)
        }

    }


    override fun toString(): String {
        return "PersonDetail(id=$id, name=$nickname, beanTestId=$beanTestId)"
    }
}
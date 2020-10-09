package com.parinherm.entity

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import org.eclipse.jface.viewers.Viewer
import kotlin.properties.Delegates

class PersonDetail (override var id: Long = 0, nickname: String, var personId: Long, petSpecies: String) : ModelObject(), IBeanDataEntity {

    var nickname: String by Delegates.observable(nickname, observer)
    var petSpecies: String by Delegates.observable(petSpecies, observer)

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> nickname
            1 -> {
                val listItem = ApplicationData.speciesList.find { it.code == petSpecies}
                "${listItem?.label}"
            }
            else -> ""
        }
    }

    class Comparator : BeansViewerComparator(), IViewerComparator {

        val nickname_index = 0
        val petSpecies_index = 1

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as PersonDetail
            val entity2 = e2 as PersonDetail
            val rc = when(propertyIndex){
                nickname_index -> entity1.nickname.compareTo(entity2.nickname)
                petSpecies_index -> compareLookups(entity1.petSpecies, entity2.petSpecies, ApplicationData.speciesList)
               else -> 0
            }
            return flipSortDirection(rc)
        }
    }


    override fun toString(): String {
        return "PersonDetail(id=$id, name=$nickname, petSpecies=$petSpecies, beanTestId=$personId)"
    }

    companion object Factory {
        fun make(beanTestId: Long) : PersonDetail {
            return PersonDetail(0, "", beanTestId, ApplicationData.speciesList[0].code)
        }
    }
}
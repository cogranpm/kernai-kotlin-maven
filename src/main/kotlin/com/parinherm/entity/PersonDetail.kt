package com.parinherm.entity

import com.parinherm.ApplicationData
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


    override fun toString(): String {
        return "PersonDetail(id=$id, name=$nickname, petSpecies=$petSpecies, beanTestId=$personId)"
    }

    companion object Factory {
        fun make(personId: Long) : PersonDetail {
            return PersonDetail(0, "", personId, ApplicationData.speciesList[0].code)
        }
    }
}
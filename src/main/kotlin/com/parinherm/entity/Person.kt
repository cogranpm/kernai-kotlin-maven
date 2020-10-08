package com.parinherm.entity

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import org.eclipse.jface.viewers.Viewer
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.properties.Delegates

class Person (override var id: Long = 0, name: String, income: BigDecimal, height: Double, age: Int, enteredDate: LocalDate, country: String, deceased: Boolean ) : ModelObject(), IBeanDataEntity {

    var name: String by Delegates.observable(name, observer)
    var income: BigDecimal by Delegates.observable(income, observer)
    var height: Double by Delegates.observable(height, observer)
    var age: Int by Delegates.observable(age, observer)
    var enteredDate: LocalDate by Delegates.observable(enteredDate, observer)
    var country: String by Delegates.observable(country, observer)
    var deceased: Boolean by Delegates.observable(deceased, observer)

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> name
            1 -> "${income}"
            2 -> "${height}"
            3 -> "${age}"
            4 -> {
                val listItem = ApplicationData.countryList.find { it.code == country }
                "${listItem?.label}"
            }
            5 -> "${enteredDate}"
            6 -> "${deceased}"
            else -> ""
        }
    }


    override fun toString(): String {
        return "Person(id=$id, name=$name, income=$income, height=$height, age=$age, country=$country, enteredDate=$enteredDate, deceased=$deceased)"
    }
}



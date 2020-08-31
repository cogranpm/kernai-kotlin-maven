package com.parinherm.entity

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import org.eclipse.jface.viewers.Viewer
import org.eclipse.jface.viewers.ViewerComparator
import org.eclipse.swt.SWT
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class BeanTest (override var id: Long = 0, name: String, income: BigDecimal, height: Double, age: Int, enteredDate: LocalDate, country: String, deceased: Boolean ) : ModelObject(), IBeanDataEntity {


    var name: String by Delegates.observable(name, observer)
    var income: BigDecimal by Delegates.observable(income, observer)
    var height: Double by Delegates.observable(height, observer)
    var age: Int by Delegates.observable(age, observer)
    var enteredDate: LocalDate by Delegates.observable(enteredDate, observer)
    var country: String by Delegates.observable(country, observer)
    var deceased: Boolean by Delegates.observable(deceased, observer)



    class Comparator : BeansViewerComparator(), IViewerComparator{

        val name_index = 0
        val income_index = 1
        val height_index = 2
        val age_index = 3
        val country_index = 4
        val enteredDate_index = 5
        val deceased_index = 6


        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as BeanTest
            val entity2 = e2 as BeanTest
            val rc = when(propertyIndex){
                name_index -> entity1.name.compareTo(entity2.name)
                income_index -> entity1.income.compareTo(entity2.income)
                height_index -> entity1.height.compareTo(entity2.height)
                age_index -> entity1.age.compareTo(entity2.age)
                country_index -> compareLookups(entity1.country, entity2.country, ApplicationData.countryList)
                enteredDate_index -> entity1.enteredDate.compareTo(entity2.enteredDate)
                deceased_index -> if(entity1.deceased == entity2.deceased) 0 else 1
                else -> 0
            }
           return flipSortDirection(rc)
        }

    }

    override fun toString(): String {
        return "BeanTest(id=$id, name=$name, income=$income, height=$height, age=$age, country=$country, enteredDate=$enteredDate, deceased=$deceased)"
    }
}



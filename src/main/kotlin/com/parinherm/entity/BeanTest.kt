package com.parinherm.entity

import java.math.BigDecimal
import java.time.LocalDate
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class BeanTest (name: String, income: BigDecimal, height: Double, age: Int, enteredDate: LocalDate, country: String, deceased: Boolean ) : ModelObject() {

    private val observer = {
        property: KProperty<*>,
        oldValue: Any,
        newValue: Any -> changeSupport.firePropertyChange(property.name, oldValue, newValue)
    }

    var name: String by Delegates.observable(name, observer)
    var income: BigDecimal by Delegates.observable(income, observer)
    var height: Double by Delegates.observable(height, observer)
    var age: Int by Delegates.observable(age, observer)
    var enteredDate: LocalDate by Delegates.observable(enteredDate, observer)
    var country: String by Delegates.observable(country, observer)
    var deceased: Boolean by Delegates.observable(deceased, observer)
}
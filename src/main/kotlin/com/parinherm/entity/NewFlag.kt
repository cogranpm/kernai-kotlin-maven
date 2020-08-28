package com.parinherm.entity

import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class NewFlag(isNew: Boolean = false) : ModelObject() {

    private val observer = {
        property: KProperty<*>,
        oldValue: Any,
        newValue: Any -> changeSupport.firePropertyChange(property.name, oldValue, newValue)
    }

    var isNew: Boolean by Delegates.observable(isNew, observer)

    init{
    }



}
package com.parinherm.entity

import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class DirtyFlag (dirty: Boolean) : ModelObject() {

    private val observer = {
        property: KProperty<*>,
        oldValue: Any,
        newValue: Any -> changeSupport.firePropertyChange(property.name, oldValue, newValue)
    }

    var dirty: Boolean by Delegates.observable(dirty, observer)

    init{
    }


}
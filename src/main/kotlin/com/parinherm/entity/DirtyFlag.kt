package com.parinherm.entity

import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class DirtyFlag (dirty: Boolean) : ModelObject() {


    private val observer = {
        property: KProperty<*>,
        oldValue: Any,
        newValue: Any -> changeSupport.firePropertyChange(property.name, oldValue, newValue)
    }


//    var dirty: Boolean = false
//        get() = field
//        set(value){
//            val oldValue: Boolean = field
//            field = value
//            this.firePropertyChange("dirty", oldValue, field)
//        }

    var dirty: Boolean by Delegates.observable(dirty, observer)

    init{
        //this.dirty = dirty
    }


    //var age: Int by Delegates.observable(age, observer)
    //var message: String by Delegates.observable(message, observer)


}
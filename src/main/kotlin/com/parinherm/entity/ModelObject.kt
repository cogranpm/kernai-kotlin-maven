package com.parinherm.entity

import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import kotlin.reflect.KProperty

open class ModelObject {

    protected val changeSupport: PropertyChangeSupport = PropertyChangeSupport(this)

    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        changeSupport.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        changeSupport.removePropertyChangeListener(listener)
    }

    fun firePropertyChange(propertyName: String, oldValue: Any, newValue: Any){
       changeSupport.firePropertyChange(propertyName, oldValue, newValue)
    }
}



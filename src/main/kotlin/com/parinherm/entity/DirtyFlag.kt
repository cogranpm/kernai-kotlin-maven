package com.parinherm.entity

class DirtyFlag (dirty: Boolean) : ModelObject() {
    var dirty: Boolean = false
        get() = field
        set(value){
            val oldValue: Boolean = field
            field = value
            this.firePropertyChange("dirty", oldValue, field)
        }

    init{
        this.dirty = dirty
    }
}
package com.parinherm.entity

import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class DirtyFlag (dirty: Boolean) : ModelObject() {


    var dirty: Boolean by Delegates.observable(dirty, observer)

    init{
    }


}
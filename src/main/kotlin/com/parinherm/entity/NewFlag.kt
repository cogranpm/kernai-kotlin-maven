package com.parinherm.entity

import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class NewFlag(isNew: Boolean = false) : ModelObject() {



    var isNew: Boolean by Delegates.observable(isNew, observer)

    init{
    }



}
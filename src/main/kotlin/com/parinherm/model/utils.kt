package com.parinherm.model

import com.parinherm.form.definitions.ViewDef
import org.stringtemplate.v4.*


fun test(viewDef: ViewDef){
    val g = STGroupFile("templates/models.stg")
    val i = g.getInstanceOf("entity_class")
    i.add("viewDef", viewDef )
    println(i.render())

}
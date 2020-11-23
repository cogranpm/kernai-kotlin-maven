package com.parinherm.model

import org.stringtemplate.v4.*


fun test(){
    val g = STGroupFile("templates/models.stg")
    val i = g.getInstanceOf("entity_class")
    i.add("name", "Fred")
    println(i.render())

}
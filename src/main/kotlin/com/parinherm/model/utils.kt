package com.parinherm.model

import org.stringtemplate.v4.*


fun test(){
    val g = STGroupFile("templates/entity_class.stg")
    val i = g.getInstanceOf("decl")
    i.add("name", "Fred")
    println(i.render())

}
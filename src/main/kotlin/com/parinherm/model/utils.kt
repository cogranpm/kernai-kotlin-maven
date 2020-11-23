package com.parinherm.model

import org.stringtemplate.v4.*


fun test(){
    //val g = STGroupFile("templates/entity_class.st")
    //val i = g.getInstanceOf("decl")
    val i =
    i.add("name", "Fred")
    println(i.render())

}
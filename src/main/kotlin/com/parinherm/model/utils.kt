package com.parinherm.model

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import com.parinherm.form.definitions.ViewDef
import org.stringtemplate.v4.*



fun test(viewDef: ViewDef){
    val g = STGroupFile("templates/models.stg")
    val i = g.getInstanceOf("entity_class")
    i.add("viewDef", viewDef )
    println(i.render())
}

fun testHbars(viewDef: ViewDef){
    val loader = ClassPathTemplateLoader()
    loader.prefix = "/templates"
    val hbars = Handlebars(loader)
    hbars.registerHelpers(TemplateHelpers())
    val template = hbars.compile("models")
    println(template.apply(viewDef))

}
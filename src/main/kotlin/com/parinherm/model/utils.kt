package com.parinherm.model

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import com.parinherm.form.definitions.ViewDef
import org.stringtemplate.v4.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

val tempOutputDirectory = System.getProperty("user.home")



fun test(viewDef: ViewDef){
    val g = STGroupFile("templates/models.stg")
    val i = g.getInstanceOf("entity_class")
    i.add("viewDef", viewDef )
    println(i.render())
}

fun testHbars(viewDef: ViewDef){
    val loader = ClassPathTemplateLoader()
    println(tempOutputDirectory)
    loader.prefix = "/templates"
    val hbars = Handlebars(loader)
    hbars.registerHelpers(TemplateHelpers())
    val template = hbars.compile("entity")
    writeTemplate(viewDef, template, viewDef.entityDef.name.capitalize() + ".kt", "entity")

    val schema = hbars.compile("schema")
    writeTemplate(viewDef, schema,viewDef.entityDef.name.capitalize() + "s.kt", "entity", "schema")
    val mapper = hbars.compile("mapper")
    writeTemplate(viewDef, mapper, "${viewDef.entityDef.name.capitalize()}Mapper.kt", "entity", "schema")
}

fun writeTemplate(viewDef: ViewDef, template: Template, fileName: String, vararg folders: String ) {
    val tempOutputPath = Paths.get(tempOutputDirectory, "models", *folders)
    Files.createDirectories(tempOutputPath)
    val filePath = tempOutputPath.resolve(fileName)
    File(filePath.toString()).writeText(template.apply(viewDef))
}
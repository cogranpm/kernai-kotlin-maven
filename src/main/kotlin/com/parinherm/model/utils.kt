package com.parinherm.model

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.HumanizeHelper
import com.github.jknack.handlebars.Template
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import com.parinherm.ApplicationData
import com.parinherm.form.definitions.ViewDef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.stringtemplate.v4.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

val tempOutputDirectory = "${ApplicationData.userPath}${File.separator}"

fun test(viewDef: ViewDef){
    val g = STGroupFile("templates/models.stg")
    val i = g.getInstanceOf("entity_class")
    i.add("viewDef", viewDef )
    ApplicationData.logError(null, i.render())
}

fun generateClasses(viewDefs: List<ViewDef>){
    MainScope().launch(Dispatchers.Main) {
        /*
        val loader = ClassPathTemplateLoader()
        loader.prefix = "/templates/plugin"
        val hbars = Handlebars(loader)
        hbars.registerHelpers(TemplateHelpers())
        HumanizeHelper.register(hbars)
         */

        viewDefs.forEach { viewDef: ViewDef ->
            makeClasses(ApplicationData.handleBarsEngine, viewDef)
            generateChildClasses(ApplicationData.handleBarsEngine, viewDef)
        }
    }
}

suspend fun generateChildClasses(hbars: Handlebars, viewDef: ViewDef){
    viewDef.childViews.forEach {
        makeClasses(hbars, it)
        generateChildClasses(hbars, it)
    }
}

suspend fun makeClasses(hbars: Handlebars, viewDef: ViewDef){
    val template = hbars.compile("entity")
    writeTemplate(viewDef, template, ApplicationData.makeCapital(viewDef.entityDef.name) + ".kt",  "entity")

    val schema = hbars.compile("schema")
    writeTemplate(viewDef, schema, ApplicationData.makeCapital(viewDef.entityDef.name) + "s.kt",  "entity", "schema")

    val mapper = hbars.compile("mapper")
    writeTemplate(viewDef, mapper, "${ApplicationData.makeCapital(viewDef.entityDef.name)}Mapper.kt", "entity", "schema")

    val view = hbars.compile("view")
    writeTemplate(viewDef, view, "${ApplicationData.makeCapital(viewDef.entityDef.name)}View.kt",  "view")

    val viewModel = hbars.compile("viewModel")
    writeTemplate(viewDef, viewModel, "${ApplicationData.makeCapital(viewDef.entityDef.name)}ViewModel.kt", "viewmodel")
}

fun writeTemplate(viewDef: ViewDef, template: Template, fileName: String, vararg folders: String ) {
    val tempOutputPath = Paths.get(tempOutputDirectory, "models", viewDef.id, *folders)
    Files.createDirectories(tempOutputPath)
    val filePath = tempOutputPath.resolve(fileName)
    File(filePath.toString()).writeText(template.apply(viewDef))
}

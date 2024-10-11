package com.parinherm.model

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.HumanizeHelper
import com.github.jknack.handlebars.cache.ConcurrentMapTemplateCache
import com.github.jknack.handlebars.io.FileTemplateLoader
import com.parinherm.ApplicationData
import com.parinherm.form.definitions.ViewDef
import com.parinherm.image.ImageUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.listDirectoryEntries

/*
import com.soywiz.korte.Template
import com.soywiz.korte.TemplateProvider
import com.soywiz.korte.Templates
 */

val outputPath = "${ApplicationData.userPath}${File.separator}${ApplicationData.SCRIPT_PATH}${File.separator}"



fun makeScriptFiles(viewDefs: List<ViewDef>): MutableList<String> {
    var targetFiles = mutableListOf<String>()
    //val loader = ClassPathTemplateLoader()
    val loader = FileTemplateLoader("${ApplicationData.userPath}${File.separator}${ApplicationData.TEMPLATE_PATH}${File.separator}${ApplicationData.version}")
    //loader.prefix = "/templates"
    val hbars = Handlebars().with(loader).with(ConcurrentMapTemplateCache().setReload(true))

    HumanizeHelper.register(hbars)
    hbars.registerHelpers(TemplateHelpers())

    viewDefs.forEach { viewDef: ViewDef ->
        val stringBuilder: StringBuilder = StringBuilder()
        val scriptDir = Paths.get(outputPath, viewDef.id)
        makeScript("bootstrap", hbars, viewDef, stringBuilder)
        makeScripts(hbars, viewDef, stringBuilder)
        viewDef.childViews.forEach {
            makeScripts(hbars, it, stringBuilder)
        }
        val targetFile = writeScript(stringBuilder, ApplicationData.bootstrapScriptFileName, scriptDir)
        targetFiles.add(targetFile)
    }
    return targetFiles
}

fun makeScripts(hbars: Handlebars, viewDef: ViewDef, stringBuilder: StringBuilder) {
    makeScript("entity", hbars, viewDef, stringBuilder)
    makeScript("schema", hbars, viewDef, stringBuilder)
    makeScript("mapper", hbars, viewDef, stringBuilder)
    makeScript("view", hbars, viewDef, stringBuilder)
    makeScript("viewModel", hbars, viewDef, stringBuilder)
}

fun makeScript(scriptName: String, hbars: Handlebars, viewDef: ViewDef, stringBuilder: StringBuilder) {
    val template = hbars.compile(scriptName)
    writeContent(stringBuilder, viewDef, template)
}

fun writeContent(stringBuilder: StringBuilder, viewDef: ViewDef, template: com.github.jknack.handlebars.Template) {
    stringBuilder.appendLine(template.apply(viewDef))
    stringBuilder.appendLine()
}

fun writeScript(stringBuilder: StringBuilder, fileName: String, scriptDir: Path) : String {
    Files.createDirectories(scriptDir)
    val filePath = scriptDir.resolve(fileName)
    val filePathResolved = filePath.toString()
    File(filePathResolved).writeText(stringBuilder.toString())
    return filePath.toString()
}



/* KORTE ENGINE VERSION - not much to recommend over handlebars at this point
class ResourceTemplateProvider(private val basePath: String) : TemplateProvider {
    override suspend fun get(template: String): String? {
        return this::class.java.classLoader.getResource(Paths.get(basePath, template).toString()).readText()
    }
}

suspend fun makeScriptFiles(viewDef: ViewDef) {
    val sb = StringBuilder()
    val sbDerived = StringBuilder()
    try {
        val renderer = Templates(ResourceTemplateProvider("templates"), cache = false)
        sb.append(renderScript(viewDef, renderer, "entity.korte"))
        sb.append("\n")
        sb.append(renderScript(viewDef, renderer, "schema.korte"))
        sb.append("\n")
        sb.append(renderScript(viewDef, renderer, "view.korte"))
        sb.append("\n")
        sb.append(renderScript(viewDef, renderer, "baseScript.korte"))
        sb.append("\n")

        sbDerived.append(renderScript(viewDef, renderer, "script.korte"))
        writeScript(viewDef, sb, sbDerived)
        sb.clear()
    } catch (e: Exception) {
        println(e.message)
    }
}



suspend fun writeScriptFile(basePath: String, derivedPath: String, baseSource: String, derivedSource: String){
    val baseFile = File(basePath)
    val derivedFile = File(derivedPath)
    try {
        if(!derivedFile.exists()){
           derivedFile.writeText(derivedSource)
        }
        baseFile.writeText(baseSource)
    } catch (e: Exception){
        println("${e.message}")
    }
}

suspend fun writeScript(viewDef: ViewDef, sourceBase: StringBuilder, sourceDerived: StringBuilder) {
    //val className = makeCapital(viewDef.entityDef.name)
    val basePath = "${ApplicationData.userPath}${File.separator}${ApplicationData.SCRIPT_PATH}${File.separator}Base${viewDef.id}.kts"
    val derivedPath = "${ApplicationData.userPath}${File.separator}${ApplicationData.SCRIPT_PATH}${File.separator}${viewDef.id}.kts"
    writeScriptFile(basePath, derivedPath, sourceBase.toString(), sourceDerived.toString())
}

suspend fun renderScript(viewDef: ViewDef, renderer: Templates, script: String) : String =
    renderer.render(script, mapOf("viewDef" to viewDef))
*/



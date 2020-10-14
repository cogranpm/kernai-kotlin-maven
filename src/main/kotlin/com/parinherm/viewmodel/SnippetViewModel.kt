package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.Snippet
import com.parinherm.entity.schema.SnippetMapper
import com.parinherm.form.FormViewModel
import com.parinherm.view.SnippetView
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder


//import org.jetbrains.kotlin.config.KotlinCompilerVersion
//import org.jetbrains.kotlin.daemon.common.threadCpuTime
//import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import javax.script.*

class SnippetViewModel (parent: CTabFolder)  : FormViewModel<Snippet>(
        SnippetView(parent, SnippetViewModel.Comparator()),
        SnippetMapper, { Snippet.make() }) {

    init {
        loadData(mapOf())
        try {
            val factory = ScriptEngineManager().getEngineByExtension("kts").factory
            println(factory.engineName)
            //val engine = factory!!.scriptEngine
            //val bindings = engine.createBindings()
            //val res1 = engine.eval("val x = 3")
            val engine = ScriptEngineManager().getEngineByExtension("kts")!!
            //val res1 = engine.eval("val x = 3")

            //println(res1)
        } catch(e: Exception) {
            println(e)
        }
    }

    class Comparator : BeansViewerComparator(), IViewerComparator {

        val name_index = 0
        val language_index = 1

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Snippet
            val entity2 = e2 as Snippet
            val rc = when (propertyIndex) {
                name_index -> entity1.name.compareTo(entity2.name)
                language_index -> compareLookups(entity1.language, entity2.language, ApplicationData.techLanguage)
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }

}
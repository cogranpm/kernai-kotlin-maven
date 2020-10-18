package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.Snippet
import com.parinherm.entity.schema.SnippetMapper
import com.parinherm.form.FormViewModel
import com.parinherm.view.SnippetView
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.layout.RowLayout
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

class SnippetViewModel (parent: CTabFolder)  : FormViewModel<Snippet>(
        SnippetView(parent, Comparator()),
        SnippetMapper, { Snippet.make() }) {

    init {
        loadData(mapOf())

        /* custom stuff to test out graal vm javascript */
        val editContainer = view.form.formsContainer.editContainer
        val toolbar = Composite(editContainer, SWT.BORDER)
        toolbar.layout = RowLayout()
        val testScriptButton = Button(toolbar, SWT.PUSH)
        testScriptButton.text = "Test Script"
        testScriptButton.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                testScript()
            }
        })
    }

    fun testScript(){
        try {
            val context = Context.newBuilder().allowAllAccess(true).build()
            //val context = Context.create()
            context.eval("js", loadScript()  )
        } catch(e: Exception) {
            println(e)
        }
    }

    fun loadScript() : String {
        val script = this::class.java.getResource("/scripts/testing.js")
        return script?.readText(Charsets.UTF_8) ?: ""
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
package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.Snippet
import com.parinherm.entity.schema.SnippetMapper
import com.parinherm.form.FormViewModel
import com.parinherm.form.widgets.SourceCodeViewer
import com.parinherm.script.GraalScriptRunner
import com.parinherm.script.KotlinScriptRunner
import com.parinherm.view.SnippetView
import org.eclipse.jface.text.DocumentEvent
import org.eclipse.jface.text.IDocumentListener
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Text
import java.lang.Exception


class SnippetViewModel(parent: CTabFolder) : FormViewModel<Snippet>(
    SnippetView(parent, Comparator()),
    SnippetMapper, { Snippet.make() }) {

    //val classLoader = Thread.currentThread().contextClassLoader
    //val engine: ScriptEngine = ScriptEngineManager(classLoader).getEngineByExtension("kts")

    val bodyWidget = view.form.formWidgets.get("body")?.widget as SourceCodeViewer

    init {
        loadData(mapOf())
        val snippetView = view as SnippetView

        bodyWidget.document.addDocumentListener(object : IDocumentListener {
            override fun documentAboutToBeChanged(p0: DocumentEvent?) {

            }

            override fun documentChanged(p0: DocumentEvent?) {
                dirtyFlag.dirty = true
                currentEntity?.body = bodyWidget.document.get()
            }
        })


        snippetView.testScriptButton.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                val textWidget = view.form.formWidgets["output"]
                if (textWidget != null && currentEntity != null){
                    Display.getDefault().asyncExec {
                        try {
                            Display.getDefault().activeShell.cursor = Display.getDefault().getSystemCursor(SWT.CURSOR_WAIT)
                            KotlinScriptRunner.run(textWidget.widget as Text, this@SnippetViewModel.currentEntity!!)

                        } catch (e: Exception) {
                            Display.getDefault().timerExec(200) { (textWidget.widget as Text).text = """
                                *************  Error *****************
                                ${e.message}
                                ${e.stackTrace}
                            """.trimIndent() };
                        }
                        finally {
                            Display.getDefault().activeShell.cursor = null
                        }
                   }

                }
            }
        })


        snippetView.graalScriptButton.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                GraalScriptRunner.run()
            }
        })


    }

    override fun changeSelection() {
        super.changeSelection()
        bodyWidget.document.set(currentEntity?.body)
    }

    override fun save() {
        currentEntity?.body = bodyWidget.document.get()
        super.save()
    }

    class Comparator : BeansViewerComparator(), IViewerComparator {

        val name_index = 0
        val language_index = 1

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Snippet
            val entity2 = e2 as Snippet
            val rc = when (propertyIndex) {
                name_index -> compareString(entity1.name, entity2.name)
                language_index -> compareLookups(entity1.language, entity2.language, ApplicationData.techLanguage)
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }

}
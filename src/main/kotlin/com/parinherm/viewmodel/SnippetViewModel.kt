package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.Snippet
import com.parinherm.entity.schema.SnippetMapper
import com.parinherm.form.FormViewModel
import com.parinherm.view.SnippetView
import org.eclipse.jface.text.Document
import org.eclipse.jface.text.DocumentEvent
import org.eclipse.jface.text.IDocument
import org.eclipse.jface.text.IDocumentListener
import org.eclipse.jface.text.source.AnnotationModel
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.Text
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Source
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import javax.script.ScriptEngineManager


class SnippetViewModel(parent: CTabFolder)  : FormViewModel<Snippet>(
    SnippetView(parent, Comparator()),
    SnippetMapper, { Snippet.make() }) {

   //val classLoader = Thread.currentThread().contextClassLoader
   //val engine: ScriptEngine = ScriptEngineManager(classLoader).getEngineByExtension("kts")



    init {
        loadData(mapOf())
        val snippetView = view as SnippetView


        /* custom stuff to test out graal vm javascript

        annotationModel.connect(document)
        snippetView.txtBody.setDocument(document, annotationModel)

        document.addDocumentListener(object: IDocumentListener {
            override fun documentAboutToBeChanged(p0: DocumentEvent?) {

            }

            override fun documentChanged(p0: DocumentEvent?) {
                currentEntity?.body = document.get()
            }
        })
         */

        snippetView.testScriptButton.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                runKotlinScript()
            }
        })


        snippetView.graalScriptButton.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                graalTestScript()
            }
        })


    }

    fun runKotlinScript(){
        with(ScriptEngineManager().getEngineByExtension("kts")) {
            //val writer = StringWriter()
            //context.writer = writer
            val bs = ByteArrayOutputStream()
            val ps = PrintStream(bs)

            // keep this to restore the output to regular
            val console = System.out

            // redirect the output to a byte stream
            System.setOut(ps)


           // eval("val x = 3")
           // val res2 = eval("x + 2")

            put("currentEntity", currentEntity)

            val result = eval(currentEntity?.body)
            System.out.flush()
            System.setOut(console)

            val outputWidget = view.form.formWidgets["output"]!!.widget as Text
            outputWidget.text = bs.toString()
        }

    }

    fun graalTestScript(){
        try {
            val context =Context.newBuilder("js").allowAllAccess(true).allowHostClassLookup { _ -> true }.allowIO(true).build()
            context.use {
                val utilsSource = Source.newBuilder("js", loadScript("utils.mjs"), "utils").buildLiteral()
                it.eval(utilsSource)
                val mainSource = Source.newBuilder("js", loadScript("testing.mjs"), "testing").buildLiteral() // .bu.mimeType("application/javascript+module")
                val bindings = context.getBindings("js")
                bindings.putMember("foo", ApplicationData)
                it.eval(mainSource)
                //it.eval("js", loadScript())

                val funcShowWindow = context.getBindings("js").getMember("showWindow")
                funcShowWindow.execute()
            }
        } catch (e: Exception) {
            println(e)
        }
    }

    fun loadScript(fileName: String) : String {
        val script = this::class.java.getResource("/scripts/$fileName")
        return script?.readText(Charsets.UTF_8) ?: ""
    }

    override fun changeSelection() {
        super.changeSelection()
        //document.set(currentEntity?.body)
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
package com.parinherm.script

import com.parinherm.ApplicationData
import com.parinherm.entity.Snippet
import com.parinherm.form.definitions.ViewDef
import com.parinherm.form.dialogs.ViewDefinitionSelector
import com.parinherm.lookups.LookupUtils
import com.parinherm.server.DefaultViewDefinitions
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Text
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Source
import java.io.ByteArrayOutputStream
import java.io.PrintStream

object GraalScriptRunner {

    private fun loadScript(fileName: String): String {
        val script = this::class.java.getResource("/scripts/$fileName")
        return script?.readText(Charsets.UTF_8) ?: ""
    }

    fun runScriptFile(outputWidget: Text, viewDef: ViewDef){
        val bs = ByteArrayOutputStream()
        val ps = PrintStream(bs)
        val context =
            Context.newBuilder(*ScriptUtils.graalLanguages.toTypedArray())
                .allowAllAccess(true)
                .allowHostClassLookup { _ -> true }
                .allowIO(true)
                .out(ps)
                .build()
        context.use {

            val utilsSource = Source.newBuilder("js", loadScript("utils.mjs"), "utils").buildLiteral()
            val bindings = context.getBindings("js")
            bindings.putMember("ApplicationData", ApplicationData)
            bindings.putMember("viewDef", viewDef);

            it.eval(utilsSource)
            val mainSource = Source.newBuilder("js", loadScript("dotnetFullStack.mjs"), "testing")
                .buildLiteral() // .bu.mimeType("application/javascript+module")
            it.eval(mainSource)
           // slight delay on the output
            Display.getDefault().timerExec(200) { outputWidget.text = bs.toString() };

        }

    }


    fun run(outputWidget: Text, snippet: Snippet) {
            val bs = ByteArrayOutputStream()
            val ps = PrintStream(bs)
            val context =
                Context.newBuilder(*ScriptUtils.graalLanguages.toTypedArray())
                    .allowAllAccess(true)
                    .allowHostClassLookup { _ -> true }
                    .allowIO(true)
                    .out(ps)
                    .build()
            context.use {

                /*
                val utilsSource = Source.newBuilder("js", loadScript("utils.mjs"), "utils").buildLiteral()
                it.eval(utilsSource)
                val mainSource = Source.newBuilder("js", loadScript("testing.mjs"), "testing")
                    .buildLiteral() // .bu.mimeType("application/javascript+module")
                it.eval(mainSource)
                val funcShowWindow = context.getBindings("js").getMember("showWindow")
                funcShowWindow.execute()
                 */

                val source = Source.newBuilder(snippet.language, snippet.body, "body").buildLiteral()
                outputWidget.text = ""
                val bindings = context.getBindings(snippet.language)
                bindings.putMember("ApplicationData", ApplicationData)
                bindings.putMember("Display", Display.getDefault())
                bindings.putMember("ViewDefinitionSelector", ViewDefinitionSelector(Display.getCurrent().activeShell));
                bindings.putMember("DefaultViewDefinitions", DefaultViewDefinitions);
                bindings.putMember("LookupUtils", LookupUtils)
                it.eval(source)
                // slight delay on the output
                Display.getDefault().timerExec(200) { outputWidget.text = bs.toString() };
            }
    }
}
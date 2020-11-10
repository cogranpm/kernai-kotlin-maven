package com.parinherm.script

import com.parinherm.ApplicationData
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Source

object GraalScriptRunner {

    private fun loadScript(fileName: String): String {
        val script = this::class.java.getResource("/scripts/$fileName")
        return script?.readText(Charsets.UTF_8) ?: ""
    }


    fun run() {
        try {
            val context =
                Context.newBuilder("js").allowAllAccess(true).allowHostClassLookup { _ -> true }.allowIO(true).build()
            context.use {
                val utilsSource = Source.newBuilder("js", loadScript("utils.mjs"), "utils").buildLiteral()
                it.eval(utilsSource)
                val mainSource = Source.newBuilder("js", loadScript("testing.mjs"), "testing")
                    .buildLiteral() // .bu.mimeType("application/javascript+module")
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
}
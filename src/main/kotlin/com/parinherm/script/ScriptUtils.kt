package com.parinherm.script

import com.parinherm.entity.Snippet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swt.SWT
import org.eclipse.swt.widgets.Text

object ScriptUtils {
    val kotlinCode = "kotlin"
    val javascriptCode = "js"
    val pythonCode = "python"
    val rCode = "r"
    val rubyCode = "ruby"
    val cSharpCode = "c#"
    val graalLanguages = listOf(javascriptCode)

    fun run(outputWidget: Text, snippet: Snippet) {
        if (snippet.language.lowercase() == kotlinCode) {
                KotlinScriptRunner.run(outputWidget, snippet)
        } else if (graalLanguages.contains(snippet.language.lowercase())) {
                GraalScriptRunner.run(outputWidget, snippet)
        }
    }
}
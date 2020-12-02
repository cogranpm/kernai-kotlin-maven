package com.parinherm.script

import com.parinherm.ApplicationData
import com.parinherm.entity.Snippet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swt.SWT
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Text
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

object KotlinScriptRunner {

    //val engine: ScriptEngine = GlobalScope.launch {  ApplicationData.makeScriptEngine()}.join()

    //val engine by lazy { makeEngine() }

//    private fun makeEngine(): ScriptEngine? {
//        return ScriptEngineManager().getEngineByExtension("kts")
//    }

    fun run(outputWidget: Text, snippet: Snippet) {

        if (ApplicationData.scriptEngine== null)
        {
            throw NullPointerException("The engine object could not be instantiated")
        }
        else {
            // run this asynchronously
            GlobalScope.launch(Dispatchers.SWT) {
                with(ApplicationData.scriptEngine)
                {
                    val bs = ByteArrayOutputStream()
                    val ps = PrintStream(bs)
                    // keep this to restore the output to regular
                    val console = System.out
                    // redirect the output to a byte stream
                    System.setOut(ps)
                    outputWidget.text = ""
                    this?.put("snippet", snippet)
                    val result = this?.eval(snippet?.body)
                    System.out.flush()
                    System.setOut(console)
                    // slight delay on the output
                    Display.getDefault().timerExec(200) { outputWidget.text = bs.toString() };
                }
            }
        }

    }

}
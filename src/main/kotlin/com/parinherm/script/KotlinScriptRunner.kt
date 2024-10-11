/********************************
 * using the ScriptEngine jsr223 part of kotlin
 * BROKEN after upgrading to kotlin 1.9 it seems to this unresolved issue
 * https://youtrack.jetbrains.com/issue/KT-63158/Kotlin-version-1.9.20-breaks-Kotlin-Interactive-Shell
 *
 *
 */

package com.parinherm.script

import com.parinherm.ApplicationData
import com.parinherm.entity.Snippet
import com.parinherm.entity.schema.SchemaBuilder
import kotlinx.coroutines.swt.SWT
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.widgets.Display
import com.parinherm.entity.MenuItem
import com.parinherm.form.dialogs.ViewDefinitionSelector
import com.parinherm.menus.TabInfo
import com.parinherm.server.DefaultViewDefinitions
import kotlinx.coroutines.*
import org.eclipse.swt.widgets.Text
import java.io.*
import javax.script.*

object KotlinScriptRunner {

    var scripts: MutableMap<String, CompiledScript> = mutableMapOf()
    val writer = StringWriter()
    val pw = PrintWriter(writer, true)
    val scriptEngine: ScriptEngine = makeScriptEngine()

    init {
        val deferred = CoroutineScope(Dispatchers.Default).launch{
            try {
                warmUpScriptEngine()
            } catch(ex: Exception){
                ApplicationData.logError(ex, "Error warming up script engine")
            }
        }
    }

    private fun makeScriptEngine(): ScriptEngine {
        val engine = ScriptEngineManager().getEngineByExtension("kts")
        engine.getContext().setWriter(pw)
        return engine
    }

    suspend fun warmUpScriptEngine() {
        scriptEngine.eval("1 + 1")
    }

    //val engine: ScriptEngine = GlobalScope.launch {  ApplicationData.makeScriptEngine()}.join()

    //val engine by lazy { makeEngine() }

    //    private fun makeEngine(): ScriptEngine? {
//        return ScriptEngineManager().getEngineByExtension("kts")
//    }
    fun runScriptFromView(tabInfo: TabInfo, viewId: String) {
        val scriptFile =
            "${ApplicationData.userPath}${File.separator}${ApplicationData.SCRIPT_PATH}${File.separator}${viewId}${File.separator}${ApplicationData.bootstrapScriptFileName}"
        runScript(tabInfo, scriptFile, viewId)
    }

    fun runScriptFromMenuItem(tabInfo: TabInfo, menuItem: MenuItem) {
        runScript(tabInfo, menuItem.scriptPath.trim(), menuItem.id.toString())
    }

    fun runScript(tabInfo: TabInfo, scriptPath: String, cacheId: String) {
        val scriptFile = File(scriptPath)
        val scriptBody = scriptFile.readText()
        MainScope().launch(Dispatchers.SWT) {
            with(scriptEngine)
            {
                this.put("ApplicationData", ApplicationData)
                this.put("SchemaBuilder", SchemaBuilder)
                //caching
                val compiled = this as Compilable
                if (!scripts.containsKey(cacheId)) {
                    val comp1: CompiledScript = compiled.compile(scriptBody)
                    scripts.put(cacheId, comp1)
                    val result = comp1.eval()
                } else {
                    val comp1 = scripts.get(cacheId)
                    comp1?.eval()
                }
                val sb = writer.buffer
                println(sb)
                val invocator = this as? Invocable
                val returnValue = invocator!!.invokeFunction("main", tabInfo)
                /*
                val schemaTables = invocator!!.invokeFunction("getSchemaTables") as List<LongIdTable>
                SchemaBuilder.buildSchema(schemaTables)
                val viewModel = invocator!!.invokeFunction("makeViewModel", parent) as IFormViewModel<*>
                ApplicationData.makeTab(viewModel, "Menu Manager", ApplicationData.TAB_KEY_FAUXRECIPE)
                 */
            }
        }
    }

    fun run(outputWidget: Text, snippet: Snippet) {
        // run this asynchronously
        with(scriptEngine)
        {
            val bs = ByteArrayOutputStream()
            val ps = PrintStream(bs)
            val console = System.out
            System.setOut(ps)
            outputWidget.text = ""
            this.put("ApplicationData", ApplicationData)
            this.put("ActiveShell", Display.getCurrent().activeShell)
            //this.put("Display", Display.getCurrent())
            this.put("ViewDefinitionSelector", ViewDefinitionSelector(Display.getCurrent().activeShell))
            this.put("DefaultViewDefinitions", DefaultViewDefinitions)
            val result = this.eval(snippet.body)
            System.out.flush()
            System.setOut(console)
            // slight delay on the output
            Display.getDefault().timerExec(200) { outputWidget.text = bs.toString() };
        }
    }

}
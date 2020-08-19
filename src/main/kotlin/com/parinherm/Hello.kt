package com.parinherm

import com.parinherm.server.SimpleHttpServer
import com.parinherm.server.SparkServer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.eclipse.swt.widgets.Display
import org.eclipse.core.databinding.observable.Realm
import org.eclipse.jface.databinding.swt.DisplayRealm

fun main(args: Array<String>) {

    /*
   val job = GlobalScope.launch {
        SparkServer.run()
   }
     */

    SimpleHttpServer.start()

    val display: Display = Display.getDefault()
    Realm.runWithDefault(DisplayRealm.getRealm(display)) {
        try {
            val window = MainWindow(null)
            window.setBlockOnOpen(true)
            window.open()
            Display.getCurrent().dispose()
        } catch (ex: Exception){
            println (ex.message)
        }


    }

    SimpleHttpServer.stop()
}


fun testid(): Int = 3



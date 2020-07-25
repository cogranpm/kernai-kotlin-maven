package com.parinherm

import org.eclipse.swt.widgets.Display
import org.eclipse.core.databinding.observable.Realm
import org.eclipse.jface.databinding.swt.DisplayRealm

fun main(args: Array<String>) {
    println("Hello, World")
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
}


fun testid(): Int = 3



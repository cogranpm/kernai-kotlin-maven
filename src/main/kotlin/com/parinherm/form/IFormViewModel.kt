package com.parinherm.form

import com.parinherm.menus.TabInfo
import org.eclipse.swt.widgets.Composite

interface IFormViewModel <T> {
    val tabInfo: TabInfo
    val tabId: String
    fun render() : Composite
    fun new()
    fun save()
    fun delete()
    fun refresh()
    fun getData(parameters: Map<String, Any>) : List<T>
    fun loadData(parameters: Map<String, Any>) : Unit
    fun createTab()
    fun activated()
    fun record()
    fun play()
    fun stop()
}
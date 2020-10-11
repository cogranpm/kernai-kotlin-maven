package com.parinherm.form

import org.eclipse.swt.widgets.Composite

interface IFormViewModel <T> {
    fun render() : Composite
    fun new()
    fun save()
    fun refresh()
    fun getData(parameters: Map<String, Any>) : List<T>
    fun loadData(parameters: Map<String, Any>) : Unit
}
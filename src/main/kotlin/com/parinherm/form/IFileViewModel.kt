package com.parinherm.form

import org.eclipse.swt.widgets.Composite

interface IFileViewModel {
    fun render() : Composite
    fun save()
    fun getData(parameters: Map<String, Any>) : String
    fun loadData(parameters: Map<String, Any>) : Unit
}
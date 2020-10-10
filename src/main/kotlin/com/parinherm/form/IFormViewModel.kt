package com.parinherm.form

import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.widgets.Composite

interface IFormViewModel {
    fun render() : Composite
    fun new()
    fun save()
    fun refresh()
}
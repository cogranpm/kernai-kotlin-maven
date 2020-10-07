package com.parinherm.form

import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.widgets.Composite

interface IFormViewModel {

    fun render(parent: CTabFolder) : Composite
    fun new()
    fun save()
}
package com.parinherm.view

import com.parinherm.form.Form
import org.eclipse.swt.widgets.Widget

interface View {
    val form: Form
    fun refresh() : Unit
    fun isValidSaveSource(widget: Widget) : Boolean
}
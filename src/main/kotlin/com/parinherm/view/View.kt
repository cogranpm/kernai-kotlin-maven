package com.parinherm.view

import com.parinherm.form.Form
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Widget

interface View {
    val form: Form
    fun refresh() : Unit
    // this needs more work, it's a toolbar now
    fun getSaveButton() : Button
}
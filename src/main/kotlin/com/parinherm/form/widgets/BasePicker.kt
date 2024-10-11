package com.parinherm.form.widgets

import org.eclipse.jface.viewers.ComboViewer
import org.eclipse.swt.widgets.Composite

interface BasePicker {
    fun makePickerWidget(parent: Composite, fieldName: String) : ComboViewer
}
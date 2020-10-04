package com.parinherm.form

import org.eclipse.swt.widgets.Control
import org.eclipse.swt.widgets.Label

data class FormWidget (val fieldName: String, val fieldType: String, val label: Label, val control: Control) {
}
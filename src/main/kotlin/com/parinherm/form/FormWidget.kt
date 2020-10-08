package com.parinherm.form

import org.eclipse.jface.viewers.ComboViewer
import org.eclipse.swt.widgets.Control
import org.eclipse.swt.widgets.Label

data class FormWidget (val fieldDef: Map<String, Any>, val fieldName: String, val fieldType: String, val label: Label, val widget: Any) {
}
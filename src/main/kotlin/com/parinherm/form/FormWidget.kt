package com.parinherm.form

import com.parinherm.form.definitions.DataTypeDef
import com.parinherm.form.definitions.FieldDef
import org.eclipse.jface.viewers.ComboViewer
import org.eclipse.swt.widgets.Control
import org.eclipse.swt.widgets.Label

data class FormWidget (val fieldDef: FieldDef, val widget: Any) {
}
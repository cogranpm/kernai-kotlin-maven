package com.parinherm.form

import com.parinherm.ApplicationData
import org.eclipse.jface.layout.TableColumnLayout
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Label


data class Form (val parent: Composite, val viewDefinition: Map<String, Any>) {

    val fields = viewDefinition[ApplicationData.ViewDef.fields] as List<Map<String, Any>>
    val composite = Composite(parent, ApplicationData.swnone)
    val sashForm = SashForm(composite, SWT.BORDER)
    val listContainer = Composite(sashForm, ApplicationData.swnone)
    val tableLayout = TableColumnLayout(true)
    val listView = getListViewer(listContainer, tableLayout)
    val columns = makeColumns(listView, fields, tableLayout )
    val editContainer = Composite(parent, ApplicationData.swnone)
    val lblErrors = Label(editContainer, ApplicationData.labelStyle)


    init {
        lblErrors.text = "hello there"
        composite.layout = FillLayout()
    }

}
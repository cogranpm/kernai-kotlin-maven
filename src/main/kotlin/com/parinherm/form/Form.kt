package com.parinherm.form

import com.parinherm.ApplicationData
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.jface.layout.TableColumnLayout
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Label


data class Form (val parent: Composite, val viewDefinition: Map<String, Any>) {

    val fields = viewDefinition[ApplicationData.ViewDef.fields] as List<Map<String, Any>>
    val root = Composite(parent, ApplicationData.swnone)
    val sashForm = SashForm(root, SWT.BORDER or SWT.VERTICAL)
    val listContainer = Composite(sashForm, ApplicationData.swnone)
    val tableLayout = TableColumnLayout(true)
    val listView = getListViewer(listContainer, tableLayout)
    val columns = makeColumns(listView, fields, tableLayout )
    val editContainer = Composite(sashForm, ApplicationData.swnone)
    val lblErrors = Label(editContainer, ApplicationData.labelStyle)
    val formInputs = makeForm(fields, editContainer)

    init {
        lblErrors.text = "hello there"
        editContainer.layout = GridLayout(2, false)
        sashForm.weights = intArrayOf(1, 2)
        sashForm.sashWidth = 4

        GridDataFactory.fillDefaults().span(2, 1).applyTo(lblErrors)
        root.layout = FillLayout(SWT.VERTICAL)
        root.layout()

    }

}
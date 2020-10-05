/* functional style file with functions for
building a user interface

idea is to return values from functions that enable all the important
widgets to be exposed for further customization without using Maps, Keys and casting

functions should be pure wherever possible, ie side effect free, but this is hard with a gui

a view class will use functions to build up a form of widgets for display
 */

package com.parinherm.form

import com.parinherm.ApplicationData
import com.parinherm.entity.LookupDetail
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.jface.layout.GridLayoutFactory
import org.eclipse.jface.layout.LayoutConstants
import org.eclipse.jface.layout.TableColumnLayout
import org.eclipse.jface.viewers.*
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.custom.CTabItem
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.layout.RowLayout
import org.eclipse.swt.widgets.*


fun getListViewer(
        parent: Composite,
        layout: TableColumnLayout
)
        : TableViewer {
    val listView = TableViewer(parent, ApplicationData.listViewStyle)

    val listTable = listView.table
    listTable.headerVisible = true
    listTable.linesVisible = true
    parent.layout = layout
    return listView
}

fun makeColumns(
        viewer: TableViewer,
        fields: List<Map<String, Any>>,
        layout: TableColumnLayout
)
        : List<TableViewerColumn> {
    return fields.map { makeColumn(it, viewer, layout) }
}


fun makeColumn(
        fieldDef: Map<String, Any>,
        viewer: TableViewer,
        layout: TableColumnLayout
): TableViewerColumn {
    val caption = fieldDef[ApplicationData.ViewDef.title] as String
    val column = TableViewerColumn(viewer, SWT.LEFT)
    val col = column.column
    col.text = caption
    col.resizable = true
    col.moveable = true
    layout.setColumnData(col, ColumnWeightData(100))
    return column
}

fun makeForm(fields: List<Map<String, Any>>, parent: Composite)
        : Map<String, FormWidget> {

    // transform list of field definitions into  a map of widgets
    // with the fieldName as the key
    return fields.map {

        val fieldName = it[ApplicationData.ViewDef.fieldName] as String
        val fieldType = it[ApplicationData.ViewDef.fieldDataType] as String

        val label = makeInputLabel(parent, it[ApplicationData.ViewDef.title] as String)
        val control = makeInputWidget(
                parent,
                fieldName,
                fieldType,
                it
        )

        // returning a map entry for each iteration
        // generates a list of pairs
        fieldName to FormWidget(fieldName, fieldType, label, control)
    }.toMap()

}

fun makeInputLabel(parent: Composite, caption: String): Label {
    val label = Label(parent, ApplicationData.labelStyle)
    label.text = caption
    GridDataFactory.fillDefaults().applyTo(label)
    return label
}

fun makeInputWidget(
        parent: Composite,
        fieldName: String,
        fieldType: String,
        fieldDef: Map<String, Any>
): Control {

    val control = when (fieldType) {
        ApplicationData.ViewDef.text -> {
            val input = Text(parent, ApplicationData.swnone)
            applyLayoutToField(input, true, false)
            input
        }
        ApplicationData.ViewDef.float -> {
            val input = Text(parent, ApplicationData.swnone)
            applyLayoutToField(input, true, false)
            input
        }
        ApplicationData.ViewDef.money -> {
            val input = Text(parent, ApplicationData.swnone)
            applyLayoutToField(input, true, false)
            input
        }
        ApplicationData.ViewDef.int -> {
            val input = Spinner(parent, ApplicationData.swnone)
            input.minimum = Integer.MIN_VALUE
            input.maximum = Integer.MAX_VALUE
            applyLayoutToField(input, false, false)
            input
        }
        ApplicationData.ViewDef.bool -> {
            val input = Button(parent, SWT.CHECK)
            applyLayoutToField(input, false, false)
            input
        }
        ApplicationData.ViewDef.datetime -> {
            val input = DateTime(parent, SWT.DROP_DOWN or SWT.DATE)
            applyLayoutToField(input, false, false)
            input
        }
        ApplicationData.ViewDef.lookup -> {
            val input = ComboViewer(parent)
            GridDataFactory.fillDefaults().grab(true, false).applyTo(input.combo)
            input.contentProvider = ArrayContentProvider.getInstance()
            input.labelProvider = (object : LabelProvider() {
                override fun getText(element: Any): String {
                    return (element as LookupDetail).label
                }
            })
            val comboSource = ApplicationData.lookups.getOrDefault(
                    fieldDef[ApplicationData.ViewDef.lookupKey] as String, listOf()
            )
            applyLayoutToField(input.control, true, false)
            input.input = comboSource
            input.control
        }
        else -> {
            // just a dummy thing should never happen
            Label(null, SWT.NONE)
        }
    }
    control.setData("fieldName", fieldName)
    return control
}

fun applyLayoutToField(widget: Control, stretchH: Boolean, stretchY: Boolean): Unit {
    GridDataFactory.fillDefaults().grab(stretchH, stretchY).applyTo(widget)
}

fun makeEditContainer(hasChildViews: Boolean, parent: Composite): FormContainer {

    val editContainer = Composite(parent, ApplicationData.swnone)
    if (hasChildViews) {
        editContainer.layout = FillLayout(SWT.VERTICAL)
        val sashForm = SashForm(editContainer, SWT.BORDER or SWT.HORIZONTAL)
        val fieldsContainer = Composite(sashForm, ApplicationData.swnone)
        val childContainer = Composite(sashForm, ApplicationData.swnone)
        childContainer.layout = FillLayout(SWT.VERTICAL)
        sashForm.weights = intArrayOf(1, 1)
        sashForm.sashWidth = 4
        fieldsContainer.layout = GridLayout(2, false)
       return FormContainer(fieldsContainer, childContainer)
    } else {
        editContainer.layout = GridLayout(2, false)
        return FormContainer(editContainer, null)
    }
}

fun makeChildFormContainer(parent: Composite, childDefs: List<Map<String, Any>>) : ChildFormContainer {
    val folder = CTabFolder(parent, SWT.TOP or SWT.BORDER)
    val childTabs = childDefs.mapIndexed { index: Int, item: Map<String, Any> ->
        val childTab = makeChildTab(folder, item)
        if (index == 0) {
            folder.selection = childTab.tab
        }
        childTab
    }
    return ChildFormContainer(folder, childTabs)
}

fun makeChildTab(folder: CTabFolder, childDefinition: Map<String, Any>): ChildFormTab{
    val tab = CTabItem(folder, SWT.CLOSE)
    tab.text = childDefinition[ApplicationData.ViewDef.title].toString()

    val childComposite = Composite(folder, ApplicationData.swnone)
    childComposite.layout = GridLayout()

    val buttonBar = Composite(childComposite, ApplicationData.swnone)

    val listComposite = Composite(childComposite, ApplicationData.swnone)
    val tableLayout = TableColumnLayout(true)

    buttonBar.layout = RowLayout()
    val btnAdd = Button(buttonBar, SWT.PUSH)
    btnAdd.text = ApplicationData.ViewDef.add_caption
    val btnRemove = Button(buttonBar, SWT.PUSH)
    btnRemove.text = "Remove"

    tab.control = childComposite

    val fields = childDefinition[ApplicationData.ViewDef.fields] as List<Map<String, Any>>
    val listView = getListViewer(listComposite, tableLayout)
    val columns = makeColumns(listView, fields, tableLayout)

    GridLayoutFactory.fillDefaults().numColumns(1).margins(LayoutConstants.getMargins()).generateLayout(childComposite)
    GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonBar)
    GridDataFactory.fillDefaults().grab(true, true).applyTo(listComposite)
    return ChildFormTab(tab, buttonBar, btnAdd, btnRemove, listComposite, listView, columns)
}

fun hasChildViews(viewDefinition: Map<String, Any>): Boolean {
    if (viewDefinition.containsKey(ApplicationData.ViewDef.childViews)) {
        val childDefs = viewDefinition[ApplicationData.ViewDef.childViews] as List<Map<String, Any>>
        return childDefs.isNotEmpty()
    } else {
        return false
    }
}


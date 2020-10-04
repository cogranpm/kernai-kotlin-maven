/* functional style file with functions for
building a user interface
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
        val label = makeInputLabel(parent, it[ApplicationData.ViewDef.title] as String)
        val fieldType = it[ApplicationData.ViewDef.fieldDataType] as String
        val control = makeInputWidget(
            parent,
            fieldType,
            it
        )
        val fieldName = it[ApplicationData.ViewDef.fieldName] as String
        control.setData("fieldName", fieldName)
        applyLayoutToField(control)
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
    fieldType: String,
    fieldDef: Map<String, Any>
): Control {

    when (fieldType) {
        ApplicationData.ViewDef.text -> {
            return Text(parent, ApplicationData.swnone)
        }
        ApplicationData.ViewDef.float -> {
            return Text(parent, ApplicationData.swnone)
        }
        ApplicationData.ViewDef.money -> {
            return Text(parent, ApplicationData.swnone)
        }
        ApplicationData.ViewDef.int -> {
            val input = Spinner(parent, ApplicationData.swnone)
            input.minimum = Integer.MIN_VALUE
            input.maximum = Integer.MAX_VALUE
            return input
        }
        ApplicationData.ViewDef.bool -> {
            return Button(parent, SWT.CHECK)
        }
        ApplicationData.ViewDef.datetime -> {
            return DateTime(parent, SWT.DROP_DOWN or SWT.DATE)
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
            input.input = comboSource
            return input.control
        }
        else -> {
            // just a dummy thing should never happen
            return Label(null, SWT.NONE)
        }
    }
}

fun applyLayoutToField(widget: Control): Unit {
    GridDataFactory.fillDefaults().grab(false, false).applyTo(widget)
}


fun makeEditContainer(
    parent: Composite,
    viewDefinition: Map<String, Any>
)
        : Composite {
    /*
    if we have master detail children then we need the edit container in horizontal sash form
    with an edit composite up top and a tab control in the below
     */
    if (hasChildViews(viewDefinition)) {
        val editContainer = Composite(parent, ApplicationData.swnone)
        editContainer.layout = FillLayout(SWT.VERTICAL)
        val sashForm = SashForm(editContainer, SWT.BORDER or SWT.HORIZONTAL)
        val fieldsContainer = Composite(sashForm, ApplicationData.swnone)
        val childContainer = Composite(sashForm, ApplicationData.swnone)
        childContainer.layout = FillLayout(SWT.VERTICAL)
        sashForm.weights = intArrayOf(1, 1)
        sashForm.sashWidth = 4
        val childDefs = viewDefinition[ApplicationData.ViewDef.childViews] as List<Map<String, Any>>
        val folder = CTabFolder(childContainer, SWT.TOP or SWT.BORDER)
        childDefs.forEachIndexed { index: Int, childDefinition: Map<String, Any> ->
            run {
                val tab = makeChildTab(folder, childDefinition)
                if (index == 0) {
                    folder.selection = tab
                }
            }
        }
        return fieldsContainer

    } else {
        return Composite(parent, ApplicationData.swnone)
    }
}

fun makeChildTab(folder: CTabFolder, childDefinition: Map<String, Any>): CTabItem {
    val tab = CTabItem(folder, SWT.CLOSE)
    tab.text = childDefinition[ApplicationData.ViewDef.title].toString()

    val childComposite = Composite(folder, ApplicationData.swnone)
    childComposite.layout = GridLayout()

    val buttonBar = Composite(childComposite, ApplicationData.swnone)

    val listComposite = Composite(childComposite, ApplicationData.swnone)
    listComposite.layout = FillLayout()

    buttonBar.layout = RowLayout()
    val btnAdd = Button(buttonBar, SWT.PUSH)
    btnAdd.text = ApplicationData.ViewDef.add_caption
    val btnRemove = Button(buttonBar, SWT.PUSH)
    btnRemove.text = "Remove"

    tab.control = childComposite

    GridLayoutFactory.fillDefaults().numColumns(1).margins(LayoutConstants.getMargins()).generateLayout(childComposite)
    GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonBar)
    GridDataFactory.fillDefaults().grab(true, true).applyTo(listComposite)
    return tab
}

fun hasChildViews(viewDefinition: Map<String, Any>): Boolean {
    if (viewDefinition.containsKey(ApplicationData.ViewDef.childViews)) {
        val childDefs = viewDefinition[ApplicationData.ViewDef.childViews] as List<Map<String, Any>>
        return childDefs.isNotEmpty()
    } else {
        return false
    }
}



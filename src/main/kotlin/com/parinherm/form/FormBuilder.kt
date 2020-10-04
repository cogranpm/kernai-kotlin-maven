/* functional style file with functions for
building a user interface
 */

package com.parinherm.form

import com.parinherm.ApplicationData
import com.parinherm.TabInstance
import com.parinherm.builders.ViewBuilder
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.jface.layout.GridLayoutFactory
import org.eclipse.jface.layout.LayoutConstants
import org.eclipse.jface.layout.TableColumnLayout
import org.eclipse.jface.viewers.ColumnWeightData
import org.eclipse.jface.viewers.TableViewer
import org.eclipse.jface.viewers.TableViewerColumn
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.custom.CTabItem
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.layout.RowLayout
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite


/* the world state so to speak
a map of TabInstances
 */

val tabInstances = mutableMapOf<String, TabInstance>()

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


fun makeEditContainer(parent: Composite,
                      viewDefinition: Map<String, Any>)
        : Composite {
    /*
    if we have master detail children then we need the edit container in horizontal sash form
    with an edit composite up top and a tab control in the below
     */
    if (hasChildViews(viewDefinition)){
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
        childDefs.forEachIndexed{
                index: Int, childDefinition: Map<String, Any> ->
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

fun makeChildTab(folder: CTabFolder, childDefinition: Map<String, Any>) : CTabItem {
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

fun hasChildViews(viewDefinition: Map<String, Any>): Boolean{
    if (viewDefinition.containsKey(ApplicationData.ViewDef.childViews)) {
        val childDefs = viewDefinition[ApplicationData.ViewDef.childViews] as List<Map<String, Any>>
        return childDefs.isNotEmpty()
    } else {
        return false
    }
}



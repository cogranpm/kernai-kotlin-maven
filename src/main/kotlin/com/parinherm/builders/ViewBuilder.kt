/************************************************************
 * BeansViewBuilder
 * a function that returns a configured SWT/JFACE composite
 * crud view that uses databinding
 * and expects a JavaBean style entity that uses PropertyChange notifications
 * because that is what JFace expects
 * it's possible to databind a simple map of key/values but the functionality is limited
 * ie how to get observable table viewer columns
 * expects a Map argument that supplies the declarative configuration of the view
 * it's expected these can be served from some kind of server in the future
 * serialized and rehydrated on the client into a view
 * idea is to allow more control from the server over the clients
 * good for automatice updates and so on
 */

package com.parinherm.builders

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

object ViewBuilder {


    fun renderView(parent: Composite, viewDefinition: Map<String, Any>, addWidget: (String, Any) -> Unit): Composite {
        val fields = viewDefinition[ApplicationData.ViewDef.fields] as List<Map<String, Any>>
        val composite = Composite(parent, ApplicationData.swnone)
        addWidget("composite", composite)
        val sashForm = SashForm(composite, SWT.BORDER)
        val listContainer = Composite(sashForm, ApplicationData.swnone)
        val editContainer = getEditContainer(sashForm, viewDefinition, addWidget)
        val listView = getListViewer(listContainer, fields, addWidget)
        addWidget("list", listView)
        getForm(fields, editContainer, addWidget)
        val lblErrors = Label(editContainer, ApplicationData.labelStyle)
        addWidget("lblErrors", lblErrors)
        val btnSave = Button(editContainer, SWT.PUSH)
        val btnNew = Button(editContainer, SWT.PUSH)

        editContainer.layout = GridLayout(2, false)
        sashForm.weights = intArrayOf(1, 2)
        sashForm.sashWidth = 4

        /* move these out to the toolbar */
        btnSave.text = "Save"
        addWidget("btnSave", btnSave)
        btnSave.enabled = false
        btnNew.text = "New"
        addWidget("btnNew", btnNew)

        GridDataFactory.fillDefaults().span(2, 1).applyTo(lblErrors)
        composite.layout = FillLayout(SWT.VERTICAL)
        composite.layout()
        return composite
    }

    private fun getForm(fields: List<Map<String, Any>>, editContainer: Composite, addWidget: (String, Any) -> Unit){
        fields.forEach { item: Map<String, Any> ->
            val label = Label(editContainer, ApplicationData.labelStyle)
            label.text = item[ApplicationData.ViewDef.title] as String
            val fieldName = item[ApplicationData.ViewDef.fieldName] as String
            GridDataFactory.fillDefaults().applyTo(label)
            when (item[ApplicationData.ViewDef.fieldDataType]) {
                ApplicationData.ViewDef.text -> {
                    val input = Text(editContainer, ApplicationData.swnone)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input)
                    addWidget(fieldName, input)
                }
                ApplicationData.ViewDef.float -> {
                    val input = Text(editContainer, ApplicationData.swnone)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input)
                    addWidget(fieldName, input)
                }
                ApplicationData.ViewDef.money -> {
                    val input = Text(editContainer, ApplicationData.swnone)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input)
                    addWidget(fieldName, input)
                }
                ApplicationData.ViewDef.int -> {
                    val input = Spinner(editContainer, ApplicationData.swnone)
                    input.minimum = Integer.MIN_VALUE
                    input.maximum = Integer.MAX_VALUE
                    GridDataFactory.fillDefaults().grab(false, false).applyTo(input)
                    addWidget(fieldName, input)
                }
                ApplicationData.ViewDef.bool -> {
                    val input = Button(editContainer, SWT.CHECK)
                    GridDataFactory.fillDefaults().grab(false, false).applyTo(input)
                    addWidget(fieldName, input)
                }
                ApplicationData.ViewDef.datetime -> {
                    val input = DateTime(editContainer, SWT.DROP_DOWN or SWT.DATE)
                    GridDataFactory.fillDefaults().grab(false, false).applyTo(input)
                    addWidget(fieldName, input)
                }
                ApplicationData.ViewDef.lookup -> {
                    val input = ComboViewer(editContainer)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input.combo)
                    input.contentProvider = ArrayContentProvider.getInstance()
                    input.labelProvider = (object : LabelProvider() {
                        override fun getText(element: Any): String {
                            return (element as LookupDetail).label
                        }
                    })
                    val comboSource = ApplicationData.lookups.getOrDefault(item[ApplicationData.ViewDef.lookupKey] as String, listOf())
                    input.input = comboSource
                    addWidget(fieldName, input)
                }
                else -> {
                }
            }
        }
    }

    private fun hasChildViews(viewDefinition: Map<String, Any>): Boolean{
        if (viewDefinition.containsKey(ApplicationData.ViewDef.childViews)) {
            val childDefs = viewDefinition[ApplicationData.ViewDef.childViews] as List<Map<String, Any>>
            if (childDefs.size > 0) {
                return true
            }
        }
        return false
    }

    private fun getListViewer(parent: Composite, fields: List<Map<String, Any>>, addWidget: (String, Any) -> Unit)
            : TableViewer {
        val listView = TableViewer(parent, ApplicationData.listViewStyle)
        val tableLayout = TableColumnLayout(true)
        fields.forEach{item: Map<String, Any> ->
           val fieldName = item[ApplicationData.ViewDef.fieldName] as String
            val column = getColumn(item[ApplicationData.ViewDef.title] as String, listView, tableLayout)
            addWidget(ApplicationData.ViewDef.makeColumnMapKey(fieldName), column)
        }
        val listTable = listView.table
        listTable.headerVisible = true
        listTable.linesVisible = true
        parent.layout = tableLayout
        return listView
    }

    private fun getEditContainer(parent: Composite, viewDefinition: Map<String, Any>, addWidget: (String, Any) -> Unit) : Composite {
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
            childDefs.forEach{
                childDefinition: Map<String, Any> -> makeChildTab(folder, childDefinition, addWidget)
            }
            return fieldsContainer

        } else {
            val editContainer = Composite(parent, ApplicationData.swnone)
            return editContainer
        }
    }


    private fun makeChildTab(folder: CTabFolder, childDefinition: Map<String, Any>, addWidget: (String, Any) -> Unit) : Unit {
        val childWidgetMap = mutableMapOf<String, Any>()
        addWidget(childDefinition[ApplicationData.ViewDef.viewid] as String, childWidgetMap)
        val tab = CTabItem(folder, SWT.CLOSE)
        tab.text = childDefinition[ApplicationData.ViewDef.title].toString()
        childWidgetMap["tab"] = tab
        // each child tab should just be a list with a header
        // the header having delete, add, edit buttons
        // each row should have double click handler to open up a new root level tab
        // allowing user full editing experience of that entity
        // duplicate the list of the child item, but limit it to the parent foreign key
        // parent foreign key should be baked in and NEVER CHANGE no matter if calling tab
        // changes it's selection, ie make the Tab completely stable with regards to the
        // children parent relationship and not have it change underneath
        // the double click / edit will just be like making a tab at top level window
        // just pass in the view definition for the child
        val childComposite = Composite(folder, ApplicationData.swnone)
        childComposite.layout = GridLayout()
        val buttonBar = Composite(childComposite, ApplicationData.swnone)
        val listComposite = Composite(childComposite, ApplicationData.swnone)
        listComposite.layout = FillLayout()
        buttonBar.layout = RowLayout()
        val btnAdd = Button(buttonBar, SWT.PUSH)
        btnAdd.text = "Add"
        childWidgetMap["btnAdd"] = btnAdd
        val btnRemove = Button(buttonBar, SWT.PUSH)
        btnRemove.text = "Remove"
        childWidgetMap["btnRemove"] = btnRemove
        val fields = childDefinition[ApplicationData.ViewDef.fields] as List<Map<String, Any>>
        val list = getListViewer(listComposite, fields, addWidget)
        childWidgetMap["list"] = list
        tab.control = childComposite

        //GridLayoutFactory.fillDefaults().generateLayout(buttonBar)
        GridLayoutFactory.fillDefaults().numColumns(1).margins(LayoutConstants.getMargins()).generateLayout(childComposite)
        GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonBar)
        GridDataFactory.fillDefaults().grab(true, true).applyTo(listComposite)
    }


   private fun getColumn(caption: String,
                          viewer: TableViewer,
                          layout: TableColumnLayout) : TableViewerColumn
    {
        val column = TableViewerColumn(viewer, SWT.LEFT)
        val col = column.column
        col.text = caption
        col.resizable = true
        col.moveable = true
        layout.setColumnData(col, ColumnWeightData(100))
        return column
    }


}



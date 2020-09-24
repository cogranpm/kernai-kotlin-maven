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

//import com.parinherm.entity.BeanTest
import com.parinherm.ApplicationData
import com.parinherm.entity.IBeanDataEntity
import com.parinherm.entity.LookupDetail
import org.eclipse.core.databinding.beans.typed.BeanProperties
import org.eclipse.core.databinding.observable.map.IObservableMap
import org.eclipse.core.databinding.property.value.IValueProperty
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.jface.layout.GridLayoutFactory
import org.eclipse.jface.layout.LayoutConstants
import org.eclipse.jface.layout.TableColumnLayout
import org.eclipse.jface.viewers.*
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.custom.CTabItem
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.layout.RowLayout
import org.eclipse.swt.widgets.*
import java.math.BigDecimal
import java.time.LocalDate

object BeansViewBuilder {

    fun <T> renderView(parent: Composite, viewState: BeansViewState<T>, viewDefinition: Map<String, Any>): Composite where T : IBeanDataEntity {

        //val form: Map<String, Any> = ApplicationData.getView(viewId)
        val composite = Composite(parent, ApplicationData.swnone)
        viewState.addWidget("composite", composite)

        val sashForm = SashForm(composite, SWT.BORDER)
        val listContainer = Composite(sashForm, ApplicationData.swnone)
        val editContainer = getEditContainer(sashForm, viewDefinition, viewState)

        val listView = getListViewer<T>(listContainer, viewDefinition, viewState)
        viewState.addWidget("list", listView)

        val fields = viewDefinition[ApplicationData.ViewDef.fields] as List<Map<String, Any>>
        fields.forEach { item: Map<String, Any> ->
            val label = Label(editContainer, ApplicationData.labelStyle)
            label.text = item[ApplicationData.ViewDef.title] as String
            val fieldName = item[ApplicationData.ViewDef.fieldName] as String
            GridDataFactory.fillDefaults().applyTo(label)
            when (item[ApplicationData.ViewDef.fieldDataType]) {
                ApplicationData.ViewDef.text -> {
                    val input = Text(editContainer, ApplicationData.swnone)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input)
                    viewState.addWidget(fieldName, input)
               }
                ApplicationData.ViewDef.float -> {
                    val input = Text(editContainer, ApplicationData.swnone)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input)
                    viewState.addWidget(fieldName, input)
               }
                ApplicationData.ViewDef.money -> {
                    val input = Text(editContainer, ApplicationData.swnone)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input)
                    viewState.addWidget(fieldName, input)
               }
                ApplicationData.ViewDef.int -> {
                    val input = Spinner(editContainer, ApplicationData.swnone)
                    input.minimum = Integer.MIN_VALUE
                    input.maximum = Integer.MAX_VALUE
                    GridDataFactory.fillDefaults().grab(false, false).applyTo(input)
                    viewState.addWidget(fieldName, input)
               }
                ApplicationData.ViewDef.bool -> {
                    val input = Button(editContainer, SWT.CHECK)
                    GridDataFactory.fillDefaults().grab(false, false).applyTo(input)
                    viewState.addWidget(fieldName, input)
               }
                ApplicationData.ViewDef.datetime -> {
                    val input = DateTime(editContainer, SWT.DROP_DOWN or SWT.DATE)
                    GridDataFactory.fillDefaults().grab(false, false).applyTo(input)
                    viewState.addWidget(fieldName, input)
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
                    viewState.addWidget(fieldName, input)
               }
                else -> {
                }
            }
       }
        val lblErrors = Label(editContainer, ApplicationData.labelStyle)
        viewState.addWidget("lblErrors", lblErrors)
        val btnSave = Button(editContainer, SWT.PUSH)
        val btnNew = Button(editContainer, SWT.PUSH)

        editContainer.layout = GridLayout(2, false)

        sashForm.weights = intArrayOf(1, 2)
        sashForm.sashWidth = 4

        btnSave.text = "Save"
        viewState.addWidget("btnSave", btnSave)
        btnSave.enabled = false

        btnNew.text = "New"
        viewState.addWidget("btnNew", btnNew)

        viewState.createViewCommands(fields)
        viewState.createListViewBindings()
        GridDataFactory.fillDefaults().span(2, 1).applyTo(lblErrors)
        composite.layout = FillLayout(SWT.VERTICAL)
        composite.layout()
        return composite
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


    private fun <T> getListViewer(parent: Composite, viewDefinition: Map<String, Any>, viewState: BeansViewState<T>) : TableViewer where T : IBeanDataEntity {
        val listView = TableViewer(parent, ApplicationData.listViewStyle)
        val tableLayout = TableColumnLayout(true)
        val contentProvider = ObservableListContentProvider<T>()

        // list of IObservableMap to make the tableviewer columns observable
        // two step operation, get observable on domain entity (BeanProperty)
        // then get the MapObservable via observeDetail on the observable
        // add it to the array below so it can be unpacked in one step outside the fields loop
        val columnLabelList: MutableList<IObservableMap<T, out Any>> = mutableListOf()
        var columnIndex = 0
        val fields = viewDefinition[ApplicationData.ViewDef.fields] as List<Map<String, Any>>
        fields.forEach { item: Map<String, Any> ->
           val fieldName = item[ApplicationData.ViewDef.fieldName] as String
            when (item[ApplicationData.ViewDef.fieldDataType]) {
                ApplicationData.ViewDef.text -> {
                    val observableColumn: IValueProperty<T, String> = BeanProperties.value<T, String>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                }
                ApplicationData.ViewDef.float -> {
                    val observableColumn: IValueProperty<T, Double> = BeanProperties.value<T, Double>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                }
                ApplicationData.ViewDef.money -> {
                   val observableColumn: IValueProperty<T, BigDecimal> = BeanProperties.value<T, BigDecimal>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                }
                ApplicationData.ViewDef.int -> {
                   val observableColumn: IValueProperty<T, Int> = BeanProperties.value<T, Int>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                }
                ApplicationData.ViewDef.bool -> {
                   val observableColumn: IValueProperty<T, Boolean> = BeanProperties.value<T, Boolean>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                }
                ApplicationData.ViewDef.datetime -> {
                   val observableColumn: IValueProperty<T, LocalDate> = BeanProperties.value<T, LocalDate>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                }
                ApplicationData.ViewDef.lookup -> {
                   val observableColumn: IValueProperty<T, String> = BeanProperties.value<T, String>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                }
                else -> {
                }
            }

            val column = getColumn(viewState.comparator, item[ApplicationData.ViewDef.title] as String, listView, tableLayout, columnIndex)
            viewState.addWidget(fieldName + "_column", column)
            columnIndex++
        }

        //observable column support, but no control over the cell contents
        //ViewerSupport.bind(listView, viewState.wl, *(columnLabelList.toTypedArray()))

        val labelMaps = columnLabelList.toTypedArray()
        val labelProvider = (object: ObservableMapLabelProvider(labelMaps){
            override fun getColumnText(element: Any?, columnIndex: Int): String {
                val beanEntity = element as IBeanDataEntity
                return beanEntity?.getColumnValueByIndex(columnIndex)
            }
       })
        listView.contentProvider = contentProvider
        listView.labelProvider = labelProvider

        val listTable = listView.table
        listTable.headerVisible = true
        listTable.linesVisible = true
        parent.layout = tableLayout
        return listView
    }


    private fun <T> getEditContainer(parent: Composite, viewDefinition: Map<String, Any>, viewState: BeansViewState<T>) : Composite  where T : IBeanDataEntity {
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
                val tab = CTabItem(folder, SWT.CLOSE)
                tab.text = it[ApplicationData.ViewDef.title].toString()
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
                buttonBar.layout = RowLayout()
                val btnAdd = Button(buttonBar, SWT.PUSH)
                btnAdd.text = "Add"
                val btnRemove = Button(buttonBar, SWT.PUSH)
                btnRemove.text = "Remove"

                //val list = TableViewer(childComposite, ApplicationData.listViewStyle)


                // should each child entity have it's own viewstate
                val list = getListViewer(childComposite, it, viewState )
                tab.control = childComposite

                //GridLayoutFactory.fillDefaults().generateLayout(buttonBar)
                GridLayoutFactory.fillDefaults().numColumns(1).margins(LayoutConstants.getMargins()).generateLayout(childComposite)
                GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonBar)
                GridDataFactory.fillDefaults().grab(true, true).applyTo(list.table)
                //GridDataFactory.swtDefaults().grab(true, true).applyTo(childComposite);


            }
            return fieldsContainer

        } else {

            val editContainer = Composite(parent, ApplicationData.swnone)
            return editContainer
        }
    }


    private fun getColumn(comparator: BeansViewerComparator, caption: String,
                          viewer: TableViewer,
                          layout: TableColumnLayout,
                          columnIndex: Int) : TableViewerColumn {
        val column = TableViewerColumn(viewer, SWT.LEFT)
        val col = column.column
        col.text = caption
        col.resizable = true
        col.moveable = true
        layout.setColumnData(col, ColumnWeightData(100))
        col.addSelectionListener(getSelectionAdapter(viewer, col, columnIndex, comparator))
        return column
    }

    private fun getSelectionAdapter(viewer: TableViewer, column: TableColumn, index: Int, comparator: BeansViewerComparator) : SelectionAdapter {
       val selectionAdapter = (object: SelectionAdapter() {
           override fun widgetSelected(e: SelectionEvent?) {
               comparator.setColumn(index)
               val dir = comparator.getDirection()
               viewer.table.sortDirection = dir
               viewer.table.sortColumn = column
               viewer.refresh()
           }
       })
        return selectionAdapter
    }


}



package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.ModelBinder
import com.parinherm.builders.ViewBuilder
import com.parinherm.entity.DirtyFlag
import com.parinherm.entity.IBeanDataEntity
import com.parinherm.entity.NewFlag
import com.parinherm.entity.schema.IMapper
import com.parinherm.entity.schema.SchemaBuilder
import org.eclipse.core.databinding.*
import org.eclipse.core.databinding.beans.typed.BeanProperties
import org.eclipse.core.databinding.observable.ChangeEvent
import org.eclipse.core.databinding.observable.IChangeListener
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.core.databinding.observable.map.IObservableMap
import org.eclipse.core.databinding.property.value.IValueProperty
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider
import org.eclipse.jface.internal.databinding.swt.SWTObservableValueDecorator
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.jface.viewers.TableViewer
import org.eclipse.jface.viewers.TableViewerColumn
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.events.SelectionListener
import org.eclipse.swt.widgets.*
import java.math.BigDecimal
import java.time.LocalDate

abstract class ViewModel <T> (data: List<T>,
                              private val comparator: BeansViewerComparator,
                              private val modelBinder: ModelBinder<T>,
                              private val mapper: IMapper<T>
                                ) where T: IBeanDataEntity {


    val wl = WritableList<T>()
    val widgets = mutableMapOf<String, Any>()
    var selectingFlag = false
    val dbc = DataBindingContext()
    var dirtyFlag: DirtyFlag = DirtyFlag(false)
    var newFlag: NewFlag = NewFlag(false)

    /* this is needed because the new button
    needs to store an item before saving adding it to the
    writable list
     */
    var currentItem: T? = null

    init {
        wl.addAll(data)
    }

    open fun render(parent: Composite, viewDefinition: Map<String, Any>): Composite {
        val composite = ViewBuilder.renderView(parent, viewDefinition, this::addWidget)
        val fields = viewDefinition[ApplicationData.ViewDef.fields] as List<Map<String, Any>>
        val listView = getWidget("list") as TableViewer
        createListViewBindings<T>(listView, fields, comparator)
        createViewCommands(listView, fields, comparator)
        listView.input = wl
        return composite
    }


    val listener: IChangeListener = IChangeListener {
        processStateChange(it)
    }

    fun addWidget(widgetKey: String, widget: Any){
        widgets[widgetKey] = widget
    }

    protected fun getWidget(widgetKey: String) : Any? = widgets[widgetKey]

    private fun processStateChange(ce: ChangeEvent){
        if(isDirtyEventType(ce.source)) {

            //debugging stuff
            /****************************************
            val source = ce.source
            when (source) {
            is SWTObservableValueDecorator<*> -> println(source.widget)
            is SWTVetoableValueDecorator  -> {
            println(source.widget)
            }
            is ViewerObservableValueDecorator<*> -> {
            println(source.viewer)
            }
            }
             ************************************************/
            dirtyFlag.dirty = true
        }
    }

    private fun isDirtyEventType(source: Any): Boolean {
        /* clicking the save button triggers a state change on the dirty flag
        therefore this should not trigger the dirtyflag to become true (chicken and egg)
        also when changing items in the model list viewer, the state changes due to a
        new entity loading, this should be ignored as well
        hence the selecting flag in this class
        checkign the widget that is the source of the binding event is the best
        way i could figure out how to interrogate the source of data binding state change events
         */
        if (selectingFlag) return false
        val btnSave = getWidget("btnSave")
        return when (source){
            is SWTObservableValueDecorator<*> -> source.widget != btnSave
            else -> true
        }
    }


    //need to make this take a type variable
    protected open fun <E>createListViewBindings(listView: TableViewer, fields: List<Map<String, Any>>, daComparator: BeansViewerComparator){

        // list of IObservableMap to make the tableviewer columns observable
        // two step operation, get observable on domain entity (BeanProperty)
        // then get the MapObservable via observeDetail on the observable
        // add it to the array below so it can be unpacked in one step outside the fields loop
        val columnLabelList: MutableList<IObservableMap<E, out Any>> = mutableListOf()
       val contentProvider = ObservableListContentProvider<E>()
        fields.forEach{item: Map<String, Any> ->
            val fieldName = item[ApplicationData.ViewDef.fieldName] as String
            when (item[ApplicationData.ViewDef.fieldDataType]) {
                ApplicationData.ViewDef.text -> {
                    val observableColumn: IValueProperty<E, String> = BeanProperties.value<E, String>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                }
                ApplicationData.ViewDef.float -> {
                    val observableColumn: IValueProperty<E, Double> = BeanProperties.value<E, Double>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                }
                ApplicationData.ViewDef.money -> {
                    val observableColumn: IValueProperty<E, BigDecimal> = BeanProperties.value<E, BigDecimal>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                }
                ApplicationData.ViewDef.int -> {
                    val observableColumn: IValueProperty<E, Int> = BeanProperties.value<E, Int>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                }
                ApplicationData.ViewDef.bool -> {
                    val observableColumn: IValueProperty<E, Boolean> = BeanProperties.value<E, Boolean>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                }
                ApplicationData.ViewDef.datetime -> {
                    val observableColumn: IValueProperty<E, LocalDate> = BeanProperties.value<E, LocalDate>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                }
                ApplicationData.ViewDef.lookup -> {
                    val observableColumn: IValueProperty<E, String> = BeanProperties.value<E, String>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                }
                else -> {
                }
            }
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
        listView.comparator = daComparator

    }

    // called by the view after the user interface elements have been created
    // need to think more about this, should this class setup the handlers?
    protected open fun createViewCommands(listView: TableViewer, fields: List<Map<String, Any>>, daComparator: BeansViewerComparator) {
        listSelectionCommand(listView, fields)
        listHeaderSelection(listView, fields, daComparator)
        val btnSave = getWidget("btnSave") as Button
        saveCommand(listView, btnSave)
        val btnNew = getWidget("btnNew") as Button
        newCommand(btnNew, fields)
        val composite = getWidget("composite") as Composite

    }

    /* default implementation of a list selection
    binds the list selection to the edit form
     */
     protected open fun listSelectionCommand(listView: TableViewer, fields: List<Map<String, Any>>){
        listView.addSelectionChangedListener { _ ->
            selectingFlag = true
            val selection = listView.structuredSelection
            val selectedItem = selection.firstElement
            // store the selected item in the list in the viewstate
            currentItem = selectedItem as T
            modelBinder.createDataBindings(dbc, fields, selectedItem, this::getWidget, listener, dirtyFlag)
            afterListSelection(listView, currentItem!!)
            Display.getDefault().timerExec(100) {
                selectingFlag = false
            }
        }
    }

    abstract fun afterListSelection (listView: TableViewer, currentItem: T) : Unit

    protected fun listHeaderSelection(listView: TableViewer, fields: List<Map<String, Any>>, daComparator: BeansViewerComparator){
        fields.forEachIndexed {index: Int, item: Map<String, Any> ->
            val fieldName = item[ApplicationData.ViewDef.fieldName] as String
            val column = widgets[ApplicationData.ViewDef.makeColumnMapKey(fieldName)] as TableViewerColumn
            column.column.addSelectionListener(getSelectionAdapter(listView, column.column, index, daComparator))
        }
    }

    private fun saveCommand(listView: TableViewer, btnSave: Button){
        btnSave.addSelectionListener(SelectionListener.widgetSelectedAdapter { _ ->
            // needed if ApplicationData.defaultUpdatePolicy = UpdateValueStrategy.POLICY_ON_REQUEST
            //viewState.dbc.updateModels()
            dirtyFlag.dirty = false
            if (currentItem?.id == 0L) {
                //currentItem?.id = (wl.size + 1L)
                mapper.save(currentItem as T)
                wl.add(currentItem)
                listView.selection = StructuredSelection(currentItem)
            } else {
                if (currentItem != null) {
                    mapper.save(currentItem as T)
                }
            }
            //println(currentItem.toString())
        })
    }

    fun newCommand(btnNew: Button, fields: List<Map<String, Any>>) {
        btnNew.addSelectionListener(SelectionListener.widgetSelectedAdapter { _ ->
            applyNewCommand(fields)
        })
    }

    fun applyNewCommand(fields: List<Map<String, Any>>) {
        val newItem = makeNewEntity()
        currentItem = newItem
        modelBinder.createDataBindings(dbc, fields, newItem, this::getWidget, listener, dirtyFlag)
    }

    abstract fun makeNewEntity() : T


    companion object Factory {

        fun closeCommand(composite: Composite){
            composite.addDisposeListener {
                println("I am being closed")
            }
        }

        protected fun getSelectionAdapter(viewer: TableViewer, column: TableColumn, index: Int, daComparator: BeansViewerComparator) : SelectionAdapter {
            val selectionAdapter = (object: SelectionAdapter() {
                override fun widgetSelected(e: SelectionEvent?) {
                    daComparator.setColumn(index)
                    val dir = daComparator.getDirection()
                    viewer.table.sortDirection = dir
                    viewer.table.sortColumn = column
                    viewer.refresh()
                }
            })
            return selectionAdapter
        }


    }

}
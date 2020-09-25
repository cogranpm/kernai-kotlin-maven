package com.parinherm.builders

import com.parinherm.entity.DirtyFlag
import com.parinherm.entity.IBeanDataEntity
import com.parinherm.entity.NewFlag
import org.eclipse.core.databinding.*
import org.eclipse.core.databinding.observable.ChangeEvent
import org.eclipse.core.databinding.observable.IChangeListener
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.internal.databinding.swt.SWTObservableValueDecorator
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.jface.viewers.TableViewer
import org.eclipse.swt.events.SelectionListener
import org.eclipse.swt.widgets.*

abstract class BeansViewState <T> (data: List<T>, val bean_maker: ()-> T,
                                   val comparator: BeansViewerComparator,
                                   val modelBinder: ModelBinder<T>) where T: IBeanDataEntity {


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

    fun render(parent: Composite, viewDefinition: Map<String, Any>) {
        val composite = BeansViewBuilder.renderView<T>(parent, viewDefinition)
    }


    val listener: IChangeListener = IChangeListener {
        processStateChange(it)
    }

    fun addWidget(widgetKey: String, widget: Any){
        widgets[widgetKey] = widget
    }

    private fun getWidget(widgetKey: String) : Any? = widgets[widgetKey]

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


    fun createListViewBindings(){
        val listView = getWidget("list") as TableViewer
        listView.input = wl
        listView.comparator = comparator
    }

    // called by the view after the user interface elements have been created
    // need to think more about this, should this class setup the handlers?
    fun createViewCommands(fields: List<Map<String, Any>>) {
        val listView = getWidget("list") as TableViewer
        listSelectionCommand(listView, fields)
        val btnSave = getWidget("btnSave") as Button
        saveCommand(listView, btnSave)
        val btnNew = getWidget("btnNew") as Button
        newCommand(btnNew, fields)
        val composite = getWidget("composite") as Composite

    }

    /* default implementation of a list selection
    binds the list selection to the edit form
     */
    private fun listSelectionCommand(listView: TableViewer, fields: List<Map<String, Any>>){
        listView.addSelectionChangedListener { _ ->
            selectingFlag = true
            val selection = listView.structuredSelection
            val selectedItem = selection.firstElement
            // store the selected item in the list in the viewstate
            currentItem = selectedItem as T
            modelBinder.createDataBindings(dbc, fields, selectedItem, this::getWidget, listener, dirtyFlag)
            //createDataBindings(fields, selectedItem as T)
            Display.getDefault().timerExec(100) {
                selectingFlag = false
            }
        }
    }

    fun saveCommand(listView: TableViewer, btnSave: Button){
        btnSave.addSelectionListener(SelectionListener.widgetSelectedAdapter { _ ->
            // needed if ApplicationData.defaultUpdatePolicy = UpdateValueStrategy.POLICY_ON_REQUEST
            //viewState.dbc.updateModels()
            dirtyFlag.dirty = false
            if (currentItem?.id == 0L) {
                currentItem?.id = (wl.size + 1L)
                wl.add(currentItem)
                listView.selection = StructuredSelection(currentItem)
            }
            //println(currentItem.toString())
        })
    }

    fun newCommand(btnNew: Button, fields: List<Map<String, Any>>) {
        btnNew.addSelectionListener(SelectionListener.widgetSelectedAdapter { _ ->
            // should probably just put ui into new mode
            val newItem = bean_maker()
            currentItem = newItem
            modelBinder.createDataBindings(dbc, fields, newItem, this::getWidget, listener, dirtyFlag)
        })
    }

    fun closeCommand(composite: Composite){
        composite.addDisposeListener {
            println("I am being closed")
        }

    }

}
package com.parinherm.form

import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.DirtyFlag
import com.parinherm.entity.IBeanDataEntity
import com.parinherm.entity.NewFlag
import com.parinherm.entity.schema.IMapper
import com.parinherm.view.View
import org.eclipse.core.databinding.DataBindingContext
import org.eclipse.core.databinding.observable.ChangeEvent
import org.eclipse.core.databinding.observable.IChangeListener
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.internal.databinding.swt.SWTObservableValueDecorator
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.jface.viewers.TableViewer
import org.eclipse.jface.viewers.TableViewerColumn
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.TableColumn

open class FormViewModel <T> (val view: View, val mapper: IMapper<T>, val entityMaker: () -> T) : IFormViewModel where T : IBeanDataEntity{

    var selectingFlag = false
    val dbc = DataBindingContext()
    var dirtyFlag: DirtyFlag = DirtyFlag(false)
    var newFlag: NewFlag = NewFlag(false)
    val dataList = WritableList<T>()
    var currentEntity: T? = null


    val stateChangeListener: IChangeListener = IChangeListener {
        processStateChange(it)
    }

    private fun processStateChange(ce: ChangeEvent) {
        if (isDirtyEventType(ce.source)) {

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
        return when (source) {
            is SWTObservableValueDecorator<*> -> true //source.widget != view.getSaveButton()
            else -> true
        }
    }

    override fun render(): Composite {
        val data = mapper.getAll(mapOf())
        dataList.clear()
        // populate the binding collection
        dataList.addAll(data)
        // should we call method on view passing data or just set the input directly?
        if (view != null) view.form.listView.input = dataList
        // implement all the event handlers on the view
        createCommands()
        return view.form.root
    }

    private fun createCommands() {
        listSelectionCommand(view.form.listView)
        listHeaderSelection(view.form.listView)
    }


    private fun listSelectionCommand(listView: TableViewer) {
        listView.addSelectionChangedListener { _ ->
            onListSelection()
        }
    }

    private fun onListSelection() {
        selectingFlag = true
        val selection = view.form.listView.structuredSelection
        if (!selection.isEmpty) {
            val selectedItem = selection.firstElement
            currentEntity = selectedItem as T
            changeSelection()
            Display.getDefault().timerExec(100) {
                selectingFlag = false
            }
        }
    }

    fun changeSelection() {
        val formBindings = makeFormBindings(dbc,
                view.form.formWidgets,
                currentEntity,
                view.form.lblErrors,
                stateChangeListener)
    }

    private fun listHeaderSelection(listView: TableViewer) {
        view.form.columns.forEachIndexed { index: Int, column: TableViewerColumn ->
            column.column.addSelectionListener(getSelectionAdapter(listView, column.column, index, view.form.comparator))
        }
    }

    private fun getSelectionAdapter(viewer: TableViewer, column: TableColumn, index: Int, comparator: BeansViewerComparator): SelectionAdapter {
        val selectionAdapter = (object : SelectionAdapter() {
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

    override fun save() {
        dirtyFlag.dirty = false
        if (currentEntity?.id == 0L) {
            mapper.save(currentEntity!!)
            dataList.add(currentEntity)
            view.form.listView.selection = StructuredSelection(currentEntity)
        } else {
            if (currentEntity != null) {
                mapper.save(currentEntity!!)
            }
        }
    }

    override fun new() {
        currentEntity = entityMaker()
        changeSelection()
    }


}
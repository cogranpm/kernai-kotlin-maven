package com.parinherm.builders

import com.parinherm.entity.BeanTest
import com.parinherm.entity.DirtyFlag
import org.eclipse.core.databinding.DataBindingContext
import org.eclipse.core.databinding.observable.ChangeEvent
import org.eclipse.core.databinding.observable.IChangeListener
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.internal.databinding.swt.SWTObservableValueDecorator
import org.eclipse.jface.layout.TableColumnLayout
import org.eclipse.jface.viewers.ColumnLabelProvider
import org.eclipse.jface.viewers.ColumnWeightData
import org.eclipse.jface.viewers.TableViewer
import org.eclipse.jface.viewers.TableViewerColumn
import org.eclipse.swt.SWT

class BeansViewState (data: List<BeanTest>){
    val wl = WritableList<BeanTest>()
    val widgets = mutableMapOf<String, Any>()
    var selectingFlag = false
    val dbc = DataBindingContext()
    var dirtyFlag: DirtyFlag = DirtyFlag(false)

    init {
        wl.addAll(data)
    }


    val listener: IChangeListener = IChangeListener {
        processStateChange(it)
    }

    fun addWidgetToViewState(widgetKey: String, widget: Any){
        widgets[widgetKey] = widget
    }

    fun getWidgetFromViewState(widgetKey: String) = widgets[widgetKey]

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
        val btnSave = getWidgetFromViewState("btnSave")
        return when (source){
            is SWTObservableValueDecorator<*> -> source.widget != btnSave
            else -> true
        }
    }


    fun getColumn(fieldName: String,
                  caption: String,
                  viewer: TableViewer,
                  layout: TableColumnLayout,
                  labelConverter: (element: Any?) -> String) : TableViewerColumn {
        val column = TableViewerColumn(viewer, SWT.LEFT)
        val col = column.column
        val colProvider = (object: ColumnLabelProvider() {
            override fun getText(element: Any?): String {
                return labelConverter(element)
            }
        })
        col.text = caption
        col.resizable = false
        col.moveable = false
        layout.setColumnData(col, ColumnWeightData(100))
        column.setLabelProvider(colProvider)
        return column
    }


}
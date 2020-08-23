package com.parinherm.builders

import com.parinherm.entity.DirtyFlag
import org.eclipse.core.databinding.DataBindingContext
import org.eclipse.core.databinding.observable.IChangeListener
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.core.databinding.observable.map.WritableMap
import org.eclipse.jface.layout.TableColumnLayout
import org.eclipse.jface.viewers.ColumnLabelProvider
import org.eclipse.jface.viewers.ColumnWeightData
import org.eclipse.jface.viewers.TableViewer
import org.eclipse.jface.viewers.TableViewerColumn
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Widget

class ViewState (val data: List<Map<String, Any>>) {

    // could be a viewer, or a control
    val widgets = mutableMapOf<String, Any>()

    //how to have a collection of databinding observables with different parameterized types
    //eg IObservableValue<String?> or <LookpuDetail>
    //val widgetBindings = mutableMapOf<String, WidgetBinding>()
    var widgetBindings: MutableMap<String, WidgetBinding<*, *>> = mutableMapOf()
    val wl = WritableList<Map<String, Any>>()
    //var wm = WritableMap<String, Any>()
    val dbc = DataBindingContext()


    //var selectionChange: Boolean = false
    var dirtyFlag: DirtyFlag = DirtyFlag(false)

    val listener: IChangeListener = IChangeListener {
        /*if (!selectionChange) {
            dirtyFlag.dirty = true
        }
         */
        dirtyFlag.dirty = true
    }

    init {
       wl.addAll(data)
    }


    fun getColumn(fieldName: String, caption: String, viewer: TableViewer, layout: TableColumnLayout) : TableViewerColumn {
        val column = TableViewerColumn(viewer, SWT.LEFT)
        val col = column.column
        val colProvider = (object: ColumnLabelProvider() {
            override fun getText(element: Any?): String {
                /* element is a map */
                val map = element as Map<String, Any>
                return map.getOrDefault(fieldName, "").toString()
            }

            /*override fun getImage(element: Any?): Image {

            }*/
        })
        col.text = caption
        col.resizable = false
        col.moveable = false
        layout.setColumnData(col, ColumnWeightData(100))
        column.setLabelProvider(colProvider)
        return column
    }

}
package com.parinherm.builders

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.parinherm.ApplicationData
import com.parinherm.ApplicationData.ViewDef
import com.parinherm.server.ViewBuilder
import com.parinherm.ApplicationData.swnone
import com.parinherm.ApplicationData.listViewStyle
import com.parinherm.ApplicationData.labelStyle
import com.parinherm.entity.LookupDetail
import org.eclipse.core.databinding.DataBindingContext
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.core.databinding.observable.map.WritableMap
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.jface.layout.TableColumnLayout
import org.eclipse.jface.viewers.ArrayContentProvider
import org.eclipse.jface.viewers.ComboViewer
import org.eclipse.jface.viewers.LabelProvider
import org.eclipse.jface.viewers.TableViewer
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.events.SelectionListener
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*


/* loads application definition from server
and is able to build a ui from it

using gson to parse the ui definitions into maps and list of maps etc

 */


object swtBuilder {

    // this is necessary because gson is a java library and has some weird kind of init thing
    inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)

   fun getViewDefinitions(): Map<String, Any>{
       val views = Gson().fromJson<Map<String, Any>>( HttpClient.getViews())
       return views
   }

    fun addWidgetToViewState(viewState: ViewState, widgetKey: String, widget: Any){
        viewState.widgets[widgetKey] = widget
    }

    fun renderView(data: List<WritableMap<String, Any>>, parent: Composite, viewId: String) : ViewState{
        val form: Map<String, Any> = ApplicationData.getView(viewId)
        val viewState = ViewState(data)
        val composite = Composite(parent, swnone)
        val sashForm = SashForm(composite, SWT.BORDER)
        val listContainer = Composite(sashForm, swnone)
        val editContainer = Composite(sashForm, swnone)
        val listView = TableViewer(listContainer, listViewStyle)
        val listTable = listView.table
        val tableLayout = TableColumnLayout(true)

        /*****************************************************************
         * fields
         */
        val fields = form[ViewDef.fields] as List<Map<String, Any>>
        fields.forEach { item: Map<String, Any> ->
            val label = Label(editContainer, labelStyle)
            label.text = item[ViewDef.title] as String
            GridDataFactory.fillDefaults().applyTo(label)
            when(item[ViewDef.fieldDataType]) {
                ViewDef.text -> {
                    val input = Text(editContainer, swnone)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input)
                    addWidgetToViewState(viewState, item[ViewDef.fieldName] as String, input)
                }
                ViewDef.bool -> {
                    val input = Text(editContainer, swnone)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input)
                }
                ViewDef.datetime -> {
                    val input = DateTime(editContainer, SWT.DROP_DOWN or SWT.DATE)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input)
                }
                ViewDef.lookup ->  {
                    val input = ComboViewer(editContainer)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input.combo)
                    input.contentProvider = ArrayContentProvider.getInstance()
                    input.labelProvider = (object: LabelProvider() {
                        override fun getText(element: Any) : String {
                            return (element as LookupDetail).label
                        }
                    })
                    input.input = ApplicationData.lookups[item[ViewDef.lookupKey]]
                    println(input.input)
                }
                else -> {}
            }

            // everyting is a list column right now
            val column = viewState.getColumn(item[ViewDef.fieldName] as String, item[ViewDef.title] as String, listView, tableLayout)
        }
        /**************************************************************************/
       listView.contentProvider = ObservableListContentProvider<Map<String, Any>>()
       listView.input = viewState.wl

       val lblErrors = Label(editContainer, labelStyle)
       val btnSave = Button(editContainer, SWT.PUSH)

        sashForm.weights = intArrayOf(1, 2)
        sashForm.sashWidth = 4
        listContainer.layout = GridLayout(1, true)
        editContainer.layout = GridLayout(2, false)
        listTable.headerVisible = true
        listTable.linesVisible = true
        listContainer.layout = tableLayout

        btnSave.text = "Save"
        btnSave.enabled = false
        btnSave.addSelectionListener(SelectionListener.widgetSelectedAdapter { _ ->
            for (item: Map<String, Any> in viewState.wl) {
               println("testing")
            }
            viewState.dirtyFlag.dirty = false
        })

        GridDataFactory.fillDefaults().span(2, 1).applyTo(lblErrors)
        composite.layout = FillLayout(SWT.VERTICAL)
        composite.layout()
        return viewState
    }


}

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
import com.parinherm.databinding.Converters
import com.parinherm.databinding.DateTimeSelectionProperty
import com.parinherm.entity.DirtyFlag
import com.parinherm.entity.LookupDetail
import org.eclipse.core.databinding.*
import org.eclipse.core.databinding.beans.typed.BeanProperties
import org.eclipse.core.databinding.observable.Observables
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.core.databinding.observable.map.WritableMap
import org.eclipse.core.databinding.observable.value.IObservableValue
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport
import org.eclipse.jface.databinding.swt.ISWTObservableValue
import org.eclipse.jface.databinding.swt.typed.WidgetProperties
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider
import org.eclipse.jface.databinding.viewers.typed.ViewerProperties
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
import java.math.BigDecimal
import java.time.LocalDate


/* loads application definition from server
and is able to build a ui from it

using gson to parse the ui definitions into maps and list of maps etc

 */


object swtBuilder {

    // this is necessary because gson is a java library and has some weird kind of init thing
    inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)


    fun renderView(data: List<WritableMap<String, Any>>, parent: Composite, viewId: String) : Composite {
        // form is a data definition of a ui screen
        // contained in lists of maps
        val form: Map<String, Any> = ApplicationData.getView(viewId)
        val viewState = ViewState(data)
        val composite = Composite(parent, swnone)
        val sashForm = SashForm(composite, SWT.BORDER)
        val listContainer = Composite(sashForm, swnone)
        val editContainer = Composite(sashForm, swnone)
        val listView = TableViewer(listContainer, listViewStyle)
        val listTable = listView.table
        val tableLayout = TableColumnLayout(true)

        val fields = form[ViewDef.fields] as List<Map<String, Any>>
        fields.forEach { item: Map<String, Any> ->
            val label = Label(editContainer, labelStyle)
            label.text = item[ViewDef.title] as String
            val fieldName = item[ViewDef.fieldName] as String
            GridDataFactory.fillDefaults().applyTo(label)
            when(item[ViewDef.fieldDataType]) {
                ViewDef.text -> {
                    val input = Text(editContainer, swnone)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input)
                    viewState.addWidgetToViewState(fieldName, input)
                    /*val target: ISWTObservableValue<String> = WidgetProperties.text<Text>(SWT.Modify).observe(
                        input)
                    val model: IObservableValue<Any> = Observables.observeMapEntry(viewState.wm, fieldName)
                    val widgetBinding: WidgetBinding<String, Any> = WidgetBinding(
                        target, model, null, null
                    )
                    viewState.widgetBindings[fieldName] = widgetBinding

                     */
                }
                ViewDef.float -> {
                    val input = Text(editContainer, swnone)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input)
                    viewState.addWidgetToViewState(fieldName, input)
                    /*
                    val target = WidgetProperties.text<Text>(SWT.Modify).observe(input)
                    val model: IObservableValue<Double> =
                        Observables.observeMapEntry(viewState.wm as WritableMap<String, Double>, fieldName)
                    val widgetBinding: WidgetBinding<String, Double> = WidgetBinding(
                        target, model, Converters.updToDouble, Converters.updFromDouble
                    )
                    viewState.widgetBindings[fieldName] = widgetBinding

                     */
               }
                ViewDef.money -> {
                    val input = Text(editContainer, swnone)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input)
                    viewState.addWidgetToViewState(fieldName, input)
               }
                ViewDef.int -> {
                    val input = Spinner(editContainer, swnone)
                    input.minimum = Integer.MIN_VALUE
                    input.maximum = Integer.MAX_VALUE
                    GridDataFactory.fillDefaults().grab(false, false).applyTo(input)
                    viewState.addWidgetToViewState(fieldName, input)
               }
                ViewDef.bool -> {
                    val input = Button(editContainer, SWT.CHECK)
                    GridDataFactory.fillDefaults().grab(false, false).applyTo(input)
                    viewState.addWidgetToViewState(fieldName, input)
               }
                ViewDef.datetime -> {
                    val input = DateTime(editContainer, SWT.DROP_DOWN or SWT.DATE)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input)
                    viewState.addWidgetToViewState(fieldName, input)
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
                    val comboSource = ApplicationData.lookups.getOrDefault(item[ViewDef.lookupKey] as String, listOf())
                    input.input = comboSource
                    viewState.addWidgetToViewState(fieldName, input)
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
        viewState.addWidgetToViewState("lblErrors", lblErrors)
       val btnSave = Button(editContainer, SWT.PUSH)

        sashForm.weights = intArrayOf(1, 2)
        sashForm.sashWidth = 4
        listContainer.layout = GridLayout(1, true)
        editContainer.layout = GridLayout(2, false)
        listTable.headerVisible = true
        listTable.linesVisible = true
        listContainer.layout = tableLayout

        listView.addSelectionChangedListener { _ ->
            viewState.selectingFlag = true
            val selection = listView.structuredSelection
            val selectedItem = selection.firstElement
            createDataBindings(viewState, selectedItem as WritableMap<String, Any>, fields)
            Display.getDefault().timerExec(100) {
                viewState.selectingFlag = false
            }
        }

        btnSave.text = "Save"
        viewState.addWidgetToViewState("btnSave", btnSave)
        btnSave.enabled = false
        btnSave.addSelectionListener(SelectionListener.widgetSelectedAdapter { _ ->
           viewState.dirtyFlag.dirty = false
            for (item: Map<String, Any> in viewState.wl) {
                println(item.keys)
                println(item.values)
            }

        })



        GridDataFactory.fillDefaults().span(2, 1).applyTo(lblErrors)
        composite.layout = FillLayout(SWT.VERTICAL)
        composite.layout()

        //createDataBindings(viewState, fields)
        return composite
    }


    // we have got to move the call site of this function out of a list selection event
    // and store the bindings in some kind of collection
    // the signature of the bindValue method of databindingcontext should give a clue
    // then clear the bindings from the context and re-add
    // for this there is an addBinding method on the context
    fun createDataBindings(viewState: ViewState, selectedItem: WritableMap<String, Any>, fields: List<Map<String, Any>>) {

        viewState.dbc.dispose()
        val bindings = viewState.dbc.validationStatusProviders
        for(binding: ValidationStatusProvider in bindings){
            if(binding is Binding){
                viewState.dbc.removeBinding(binding)
            }
        }

        // having real trouble storing a collection if bindings
        // the updateValuestrategy parameters on the end mess it up
        // the bindValue call can't infer the type
        /*viewState.widgetBindings.forEach { key, value ->
            val widgetBinding = value
            val bindInput = viewState.dbc.bindValue(value.bindingTarget, value.bindingModel,
                value.targetToModel, value.modelToTarget)

        }

         */

        fields.forEach { item: Map<String, Any> ->
            val fieldName = item[ViewDef.fieldName] as String
            when (item[ViewDef.fieldDataType]) {
                ViewDef.text -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as Text
                    val target: ISWTObservableValue<String> = WidgetProperties.text<Text>(SWT.Modify).observe(
                        input)
                    val model: IObservableValue<Any> = Observables.observeMapEntry(selectedItem, fieldName)
                   val bindInput = viewState.dbc.bindValue(target, model)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                }
                ViewDef.float -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as Text
                    val target = WidgetProperties.text<Text>(SWT.Modify).observe(input)
                    val model: IObservableValue<Double> =
                        Observables.observeMapEntry(selectedItem as WritableMap<String, Double>, fieldName)
                    val bindHeight = viewState.dbc.bindValue<String, Double>(
                        target, model,
                        Converters.updToDouble,
                        Converters.updFromDouble
                    )
                    ControlDecorationSupport.create(bindHeight, SWT.TOP or SWT.LEFT)
                }
                ViewDef.money -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as Text
                    val target = WidgetProperties.text<Text>(SWT.Modify).observe(input)
                    val model: IObservableValue<BigDecimal> =
                        Observables.observeMapEntry(selectedItem as WritableMap<String, BigDecimal>, fieldName)
                    val bindInput = viewState.dbc.bindValue(
                        target, model,
                        Converters.updToBigDecimal,
                        Converters.updFromBigDecimal
                    )
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                }
                ViewDef.int -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as Spinner
                    val target = WidgetProperties.spinnerSelection().observe(input)
                    //val model = Observables.observeMapEntry(viewState.wm as WritableMap<String, Int>, fieldName)
                    val model = Observables.observeMapEntry(selectedItem as WritableMap<String, Int>, fieldName)
                    val bindInput = viewState.dbc.bindValue<Int, Int>(target, model)
                }
                ViewDef.bool -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as Button
                    val target = WidgetProperties.buttonSelection().observe(input)
                    val model = Observables.observeMapEntry(selectedItem as WritableMap<String, Boolean>, fieldName)
                    val bindInput = viewState.dbc.bindValue(target, model)
                }
                ViewDef.datetime -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as DateTime
                    val inputProperty: DateTimeSelectionProperty = DateTimeSelectionProperty()
                    val target = inputProperty.observe(input)
                    val model = Observables.observeMapEntry(selectedItem as WritableMap<String, LocalDate>, fieldName)
                    val bindInput = viewState.dbc.bindValue(target, model)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                }
                ViewDef.lookup -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as ComboViewer
                    val comboSource = ApplicationData.lookups.getOrDefault(item[ViewDef.lookupKey] as String, listOf())
                    val target: IObservableValue<LookupDetail> =
                        ViewerProperties.singleSelection<ComboViewer, LookupDetail>().observeDelayed(1, input)
                    val model = Observables.observeMapEntry(selectedItem as WritableMap<String, String>, fieldName)
                    val bindInput = viewState.dbc.bindValue(
                        target, model,
                        UpdateValueStrategy.create<LookupDetail, String>(Converters.convertFromLookup),
                        UpdateValueStrategy.create<String, LookupDetail>(Converters.convertToLookup(comboSource))
                    )
                }
                else -> {
                }
            }

            viewState.dbc.bindings.forEach {
                it.target.addChangeListener(viewState.listener)
            }

            val  errorObservable: IObservableValue<String> = WidgetProperties.text<Label>().observe(
                viewState.getWidgetFromViewState("lblErrors") as Label)
            val allValidationBinding: Binding = viewState.dbc.bindValue(errorObservable,
                AggregateValidationStatus(viewState.dbc.bindings, AggregateValidationStatus.MAX_SEVERITY), null, null)


            //save button binding
            val targetSave = WidgetProperties.enabled<Button>().observe(
                viewState.getWidgetFromViewState("btnSave") as Button)
            val modelDirty = BeanProperties.value<DirtyFlag, Boolean>("dirty").observe(viewState.dirtyFlag)
            val bindSave = viewState.dbc.bindValue(targetSave, modelDirty)


        }

    }


}

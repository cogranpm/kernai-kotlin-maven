package com.parinherm.builders

import com.parinherm.ApplicationData
import com.parinherm.databinding.Converters
import com.parinherm.databinding.DateTimeSelectionProperty
import com.parinherm.entity.BeanTest
import com.parinherm.entity.DirtyFlag
import com.parinherm.entity.LookupDetail
import org.eclipse.core.databinding.AggregateValidationStatus
import org.eclipse.core.databinding.Binding
import org.eclipse.core.databinding.UpdateValueStrategy
import org.eclipse.core.databinding.ValidationStatusProvider
import org.eclipse.core.databinding.beans.typed.BeanProperties
import org.eclipse.core.databinding.observable.Observables
import org.eclipse.core.databinding.observable.map.WritableMap
import org.eclipse.core.databinding.observable.set.IObservableSet
import org.eclipse.core.databinding.observable.value.IObservableValue
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport
import org.eclipse.jface.databinding.swt.ISWTObservableValue
import org.eclipse.jface.databinding.swt.typed.WidgetProperties
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider
import org.eclipse.jface.databinding.viewers.typed.ViewerProperties
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.jface.layout.TableColumnLayout
import org.eclipse.jface.viewers.*
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.events.SelectionListener
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*
import java.math.BigDecimal
import java.time.LocalDate

object BeansViewBuilder {

    fun renderView(parent: Composite, data: List<BeanTest>, viewId: String): Composite {
        val viewState = BeansViewState(data)
        val form: Map<String, Any> = ApplicationData.getView(viewId)
        val composite = Composite(parent, ApplicationData.swnone)
        val sashForm = SashForm(composite, SWT.BORDER)
        val listContainer = Composite(sashForm, ApplicationData.swnone)
        val editContainer = Composite(sashForm, ApplicationData.swnone)
        val listView = TableViewer(listContainer, ApplicationData.listViewStyle)
        val listTable = listView.table
        val tableLayout = TableColumnLayout(true)

        val fields = form[ApplicationData.ViewDef.fields] as List<Map<String, Any>>
        fields.forEach { item: Map<String, Any> ->
            val label = Label(editContainer, ApplicationData.labelStyle)
            label.text = item[ApplicationData.ViewDef.title] as String
            val fieldName = item[ApplicationData.ViewDef.fieldName] as String
            GridDataFactory.fillDefaults().applyTo(label)
            when (item[ApplicationData.ViewDef.fieldDataType]) {
                ApplicationData.ViewDef.text -> {
                    val input = Text(editContainer, ApplicationData.swnone)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input)
                    viewState.addWidgetToViewState(fieldName, input)
                }
                ApplicationData.ViewDef.float -> {
                    val input = Text(editContainer, ApplicationData.swnone)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input)
                    viewState.addWidgetToViewState(fieldName, input)
                }
                ApplicationData.ViewDef.money -> {
                    val input = Text(editContainer, ApplicationData.swnone)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input)
                    viewState.addWidgetToViewState(fieldName, input)
                }
                ApplicationData.ViewDef.int -> {
                    val input = Spinner(editContainer, ApplicationData.swnone)
                    input.minimum = Integer.MIN_VALUE
                    input.maximum = Integer.MAX_VALUE
                    GridDataFactory.fillDefaults().grab(false, false).applyTo(input)
                    viewState.addWidgetToViewState(fieldName, input)
                }
                ApplicationData.ViewDef.bool -> {
                    val input = Button(editContainer, SWT.CHECK)
                    GridDataFactory.fillDefaults().grab(false, false).applyTo(input)
                    viewState.addWidgetToViewState(fieldName, input)
                }
                ApplicationData.ViewDef.datetime -> {
                    val input = DateTime(editContainer, SWT.DROP_DOWN or SWT.DATE)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input)
                    viewState.addWidgetToViewState(fieldName, input)
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
                    viewState.addWidgetToViewState(fieldName, input)
                }
                else -> {
                }
            }

            val column = viewState.getColumn(
                    item[ApplicationData.ViewDef.fieldName] as String,
                    item[ApplicationData.ViewDef.title] as String,
                    listView,
                    tableLayout,
                    item[ApplicationData.ViewDef.fieldLabelConverter] as (_: Any?) -> String)
        }
        val contentProvider = ObservableListContentProvider<Map<String, Any>>()
        listView.contentProvider = contentProvider
        val knownElements: IObservableSet<Map<String, Any>> = contentProvider.knownElements
        val firstName = BeanProperties.value<BeanTest, String>("name").observeDetail(knownElements)
        val labelMaps = arrayOf(1)
        /*
            val labelProvider = (object: ObservableMapLabelProvider(labelMaps) {
                override fun getText(element: Any?): String {
                    //return super.getText(element)
                    return "testing"
                }
            })

             */


        listView.input = viewState.wl

        val lblErrors = Label(editContainer, ApplicationData.labelStyle)
        viewState.addWidgetToViewState("lblErrors", lblErrors)
        val btnSave = Button(editContainer, SWT.PUSH)
        val btnNew = Button(editContainer, SWT.PUSH)

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
            createDataBindings(viewState, selectedItem as BeanTest, fields)
            Display.getDefault().timerExec(100) {
                viewState.selectingFlag = false
            }
        }

        btnSave.text = "Save"
        viewState.addWidgetToViewState("btnSave", btnSave)
        btnSave.enabled = false
        btnSave.addSelectionListener(SelectionListener.widgetSelectedAdapter { _ ->
            viewState.dirtyFlag.dirty = false

        })

        btnNew.text = "New"
        btnNew.addSelectionListener(SelectionListener.widgetSelectedAdapter { _ ->
            // should probably just put ui into new mode
            //val newItem = data.make()
            //viewState.wl.add(newItem)
            //listView.setSelection(StructuredSelection(newItem))
        })

        composite.addDisposeListener {
            println("I am being closed")
        }
        GridDataFactory.fillDefaults().span(2, 1).applyTo(lblErrors)
        composite.layout = FillLayout(SWT.VERTICAL)
        composite.layout()
        //createDataBindings(viewState, fields)
        return composite
    }

    fun createDataBindings(viewState: BeansViewState, selectedItem: BeanTest, fields: List<Map<String, Any>>) {

        viewState.dbc.dispose()
        val bindings = viewState.dbc.validationStatusProviders
        for (binding: ValidationStatusProvider in bindings) {
            if (binding is Binding) {
                viewState.dbc.removeBinding(binding)
            }
        }

        fields.forEach { item: Map<String, Any> ->
            val fieldName = item[ApplicationData.ViewDef.fieldName] as String
            when (item[ApplicationData.ViewDef.fieldDataType]) {
                ApplicationData.ViewDef.text -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as Text
                    val target: ISWTObservableValue<String> = WidgetProperties.text<Text>(SWT.Modify).observe(
                            input)
                    //val model: IObservableValue<Any> = Observables.observeMapEntry(selectedItem, fieldName)
                    val model = BeanProperties.value<BeanTest, String>(fieldName).observe(selectedItem)
                    val bindInput = viewState.dbc.bindValue(target, model)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                }
                ApplicationData.ViewDef.float -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as Text
                    val target = WidgetProperties.text<Text>(SWT.Modify).observe(input)
                    val model: IObservableValue<Double> = BeanProperties.value<BeanTest, Double>(fieldName).observe(selectedItem)
                    val bindHeight = viewState.dbc.bindValue<String, Double>(
                            target, model,
                            Converters.updToDouble,
                            Converters.updFromDouble
                    )
                    ControlDecorationSupport.create(bindHeight, SWT.TOP or SWT.LEFT)
                }
                ApplicationData.ViewDef.money -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as Text
                    val target = WidgetProperties.text<Text>(SWT.Modify).observe(input)
                    val model: IObservableValue<BigDecimal> = BeanProperties.value<BeanTest, BigDecimal>(fieldName).observe(selectedItem)
                    val bindInput = viewState.dbc.bindValue(
                            target, model,
                            Converters.updToBigDecimal,
                            Converters.updFromBigDecimal
                    )
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                }
                ApplicationData.ViewDef.int -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as Spinner
                    val target = WidgetProperties.spinnerSelection().observe(input)
                    val model = BeanProperties.value<BeanTest, Int>(fieldName).observe(selectedItem)
                    val bindInput = viewState.dbc.bindValue<Int, Int>(target, model)
                }
                ApplicationData.ViewDef.bool -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as Button
                    val target = WidgetProperties.buttonSelection().observe(input)
                    val model =  BeanProperties.value<BeanTest, Boolean>(fieldName).observe(selectedItem)
                    val bindInput = viewState.dbc.bindValue(target, model)
                }
                ApplicationData.ViewDef.datetime -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as DateTime
                    val inputProperty: DateTimeSelectionProperty = DateTimeSelectionProperty()
                    val target = inputProperty.observe(input)
                    val model = BeanProperties.value<BeanTest, LocalDate>(fieldName).observe(selectedItem)
                    val bindInput = viewState.dbc.bindValue(target, model)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                }
                ApplicationData.ViewDef.lookup -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as ComboViewer
                    val comboSource = ApplicationData.lookups.getOrDefault(item[ApplicationData.ViewDef.lookupKey] as String, listOf())
                    val target: IObservableValue<LookupDetail> =
                            ViewerProperties.singleSelection<ComboViewer, LookupDetail>().observeDelayed(1, input)
                    val model = BeanProperties.value<BeanTest, String>(fieldName).observe(selectedItem)
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

            val errorObservable: IObservableValue<String> = WidgetProperties.text<Label>().observe(
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


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
import com.parinherm.databinding.Converters
import com.parinherm.databinding.Converters.bigDecimalValidator
import com.parinherm.databinding.Converters.floatValidator
import com.parinherm.databinding.DateTimeSelectionProperty
import com.parinherm.databinding.FloatValidation
import com.parinherm.databinding.ValidationRequired
//import com.parinherm.entity.BeanTest
import com.parinherm.entity.DirtyFlag
import com.parinherm.entity.LookupDetail
import org.eclipse.core.databinding.AggregateValidationStatus
import org.eclipse.core.databinding.Binding
import org.eclipse.core.databinding.UpdateValueStrategy
import org.eclipse.core.databinding.ValidationStatusProvider
import org.eclipse.core.databinding.beans.IBeanValueProperty
import org.eclipse.core.databinding.beans.typed.BeanProperties
import org.eclipse.core.databinding.beans.typed.PojoProperties
import org.eclipse.core.databinding.conversion.text.NumberToStringConverter
import org.eclipse.core.databinding.conversion.text.StringToNumberConverter
import org.eclipse.core.databinding.observable.Observables
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.core.databinding.observable.map.IObservableMap
import org.eclipse.core.databinding.observable.map.WritableMap
import org.eclipse.core.databinding.observable.set.IObservableSet
import org.eclipse.core.databinding.observable.value.IObservableValue
import org.eclipse.core.databinding.property.Properties
import org.eclipse.core.databinding.property.value.IValueProperty
import org.eclipse.core.databinding.validation.IValidator
import org.eclipse.core.databinding.validation.ValidationStatus
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport
import org.eclipse.jface.databinding.swt.ISWTObservableValue
import org.eclipse.jface.databinding.swt.typed.WidgetProperties
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider
import org.eclipse.jface.databinding.viewers.ViewerSupport
import org.eclipse.jface.databinding.viewers.typed.ViewerProperties
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.jface.layout.TableColumnLayout
import org.eclipse.jface.viewers.*
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.events.SelectionListener
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*
import java.math.BigDecimal
import java.time.LocalDate

object BeansViewBuilder {

    fun <T> renderView(parent: Composite, viewState: BeansViewState<T>, viewId: String): Composite {

        val form: Map<String, Any> = ApplicationData.getView(viewId)
        val composite = Composite(parent, ApplicationData.swnone)
        val sashForm = SashForm(composite, SWT.BORDER)
        val listContainer = Composite(sashForm, ApplicationData.swnone)
        val editContainer = Composite(sashForm, ApplicationData.swnone)
        val listView = TableViewer(listContainer, ApplicationData.listViewStyle)
        val listTable = listView.table
        val tableLayout = TableColumnLayout(true)

        val contentProvider = ObservableListContentProvider<T>()

        // list of IObservableMap to make the tableviewer columns observable
        // two step operation, get observable on domain entity (BeanProperty)
        // then get the MapObservable via observeDetail on the observable
        // add it to the array below so it can be unpacked in one step outside the fields loop
        val columnLabelList: MutableList<IObservableMap<T, out Any>> = mutableListOf()

        // need a list of converters for column label content, ie from domain entity property to a string
        // ie from a Double to a String, this list is referenced by index when rendering the column label text
        val labelConverterList: MutableList<(element: Any?) -> String> = mutableListOf()
        var columnIndex = 0

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
                    
                    /* column observables */
                    val observableColumn: IValueProperty<T, String> = BeanProperties.value<T, String>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                    labelConverterList.add(item[ApplicationData.ViewDef.fieldLabelConverter] as (_: Any?) -> String)
                }
                ApplicationData.ViewDef.float -> {
                    val input = Text(editContainer, ApplicationData.swnone)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input)
                    viewState.addWidgetToViewState(fieldName, input)

                    /* column observables */
                    val observableColumn: IValueProperty<T, Double> = BeanProperties.value<T, Double>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                    labelConverterList.add(item[ApplicationData.ViewDef.fieldLabelConverter] as (_: Any?) -> String)
                }
                ApplicationData.ViewDef.money -> {
                    val input = Text(editContainer, ApplicationData.swnone)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input)
                    viewState.addWidgetToViewState(fieldName, input)

                    /* column observables */
                    val observableColumn: IValueProperty<T, BigDecimal> = BeanProperties.value<T, BigDecimal>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                    labelConverterList.add(item[ApplicationData.ViewDef.fieldLabelConverter] as (_: Any?) -> String)
                }
                ApplicationData.ViewDef.int -> {
                    val input = Spinner(editContainer, ApplicationData.swnone)
                    input.minimum = Integer.MIN_VALUE
                    input.maximum = Integer.MAX_VALUE
                    GridDataFactory.fillDefaults().grab(false, false).applyTo(input)
                    viewState.addWidgetToViewState(fieldName, input)
                    
                    /* column observables */
                    val observableColumn: IValueProperty<T, Int> = BeanProperties.value<T, Int>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                    labelConverterList.add(item[ApplicationData.ViewDef.fieldLabelConverter] as (_: Any?) -> String)
                }
                ApplicationData.ViewDef.bool -> {
                    val input = Button(editContainer, SWT.CHECK)
                    GridDataFactory.fillDefaults().grab(false, false).applyTo(input)
                    viewState.addWidgetToViewState(fieldName, input)

                    /* column observables */
                    val observableColumn: IValueProperty<T, Boolean> = BeanProperties.value<T, Boolean>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                    labelConverterList.add(item[ApplicationData.ViewDef.fieldLabelConverter] as (_: Any?) -> String)
                }
                ApplicationData.ViewDef.datetime -> {
                    val input = DateTime(editContainer, SWT.DROP_DOWN or SWT.DATE)
                    GridDataFactory.fillDefaults().grab(false, false).applyTo(input)
                    viewState.addWidgetToViewState(fieldName, input)
                    /* column observables */
                    val observableColumn: IValueProperty<T, LocalDate> = BeanProperties.value<T, LocalDate>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                    labelConverterList.add(item[ApplicationData.ViewDef.fieldLabelConverter] as (_: Any?) -> String)
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

                    /* column observables */
                    val observableColumn: IValueProperty<T, String> = BeanProperties.value<T, String>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                    labelConverterList.add(item[ApplicationData.ViewDef.fieldLabelConverter] as (_: Any?) -> String)
                }
                else -> {
                }
            }

            val column = getColumn(viewState.comparator, item[ApplicationData.ViewDef.title] as String, listView, tableLayout, columnIndex)
            viewState.addWidgetToViewState(fieldName + "_column", column)
            columnIndex++
        }

        //observable column support, but no control over the cell contents
        //ViewerSupport.bind(listView, viewState.wl, *(columnLabelList.toTypedArray()))
        val labelMaps = columnLabelList.toTypedArray()
        val labelProvider = (object: ObservableMapLabelProvider(labelMaps){
            override fun getColumnText(element: Any?, columnIndex: Int): String {
                return "${labelConverterList[columnIndex](element)}"
            }

            /*override fun getColumnImage(element: Any?, columnIndex: Int): Image {
                return super.getColumnImage(element, columnIndex)
            }
             */
        })
        listView.contentProvider = contentProvider
        listView.labelProvider = labelProvider
        listView.input = viewState.wl
        listView.comparator = viewState.comparator

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
            createDataBindings(viewState, selectedItem as T, fields)
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

    private fun getSelectionAdapter (viewer: TableViewer, column: TableColumn, index: Int, comparator: BeansViewerComparator) : SelectionAdapter {
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



    fun <T> createDataBindings(viewState: BeansViewState<T>, selectedItem: T, fields: List<Map<String, Any>>) {

        viewState.dbc.dispose()
        val bindings = viewState.dbc.validationStatusProviders
        for (binding: ValidationStatusProvider in bindings) {
            if (binding is Binding) {
                viewState.dbc.removeBinding(binding)
            }
        }

        fields.forEach { item: Map<String, Any> ->
            val fieldTitle = item[ApplicationData.ViewDef.title] as String
            val fieldName = item[ApplicationData.ViewDef.fieldName] as String
            when (item[ApplicationData.ViewDef.fieldDataType]) {
                ApplicationData.ViewDef.text -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as Text
                    val target = WidgetProperties.text<Text>(SWT.Modify).observe(input)
                    val model = BeanProperties.value<T, String>(fieldName).observe(selectedItem)
                    val modelToTarget = UpdateValueStrategy<String?, String?>(UpdateValueStrategy.POLICY_UPDATE)
                    val targetToModel = UpdateValueStrategy<String?, String?>(UpdateValueStrategy.POLICY_UPDATE)
                    if (item[ApplicationData.ViewDef.required] as Boolean) {
                        targetToModel.setAfterConvertValidator(ValidationRequired(fieldTitle))
                    }
                    val bindInput = viewState.dbc.bindValue(target, model, targetToModel, modelToTarget)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)

                }
                ApplicationData.ViewDef.float -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as Text
                    val target = WidgetProperties.text<Text>(SWT.Modify).observe(input)
                    val model: IObservableValue<Double> = BeanProperties.value<T, Double>(fieldName).observe(selectedItem)
                    val targetToModel = UpdateValueStrategy<String?, Double?>(UpdateValueStrategy.POLICY_UPDATE)
                    val modelToTarget = UpdateValueStrategy<Double?, String?>(UpdateValueStrategy.POLICY_UPDATE)
                    targetToModel.setConverter(StringToNumberConverter.toDouble(true))
                    targetToModel.setAfterGetValidator(FloatValidation(fieldTitle))
                    modelToTarget.setConverter(NumberToStringConverter.fromDouble(true))
                    val bindInput = viewState.dbc.bindValue<String, Double>(target, model, targetToModel, modelToTarget)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                }
                ApplicationData.ViewDef.money -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as Text
                    val target = WidgetProperties.text<Text>(SWT.Modify).observe(input)
                    val model: IObservableValue<BigDecimal> = BeanProperties.value<T, BigDecimal>(fieldName).observe(selectedItem)
                    val targetToModel = UpdateValueStrategy<String?, BigDecimal?>(UpdateValueStrategy.POLICY_UPDATE)
                    val modelToTarget = UpdateValueStrategy<BigDecimal?, String?>(UpdateValueStrategy.POLICY_UPDATE)
                    targetToModel.setAfterGetValidator(bigDecimalValidator)
                    val bindInput = viewState.dbc.bindValue(target, model, targetToModel, modelToTarget)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                }
                ApplicationData.ViewDef.int -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as Spinner
                    val target = WidgetProperties.spinnerSelection().observe(input)
                    val model = BeanProperties.value<T, Int>(fieldName).observe(selectedItem)
                    val targetToModel = UpdateValueStrategy<Int?, Int?>(UpdateValueStrategy.POLICY_UPDATE)
                    val modelToTarget = UpdateValueStrategy<Int?, Int?>(UpdateValueStrategy.POLICY_UPDATE)
                    val bindInput = viewState.dbc.bindValue<Int, Int>(target, model, targetToModel, modelToTarget)
                }
                ApplicationData.ViewDef.bool -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as Button
                    val target = WidgetProperties.buttonSelection().observe(input)
                    val model =  BeanProperties.value<T, Boolean>(fieldName).observe(selectedItem)
                    val targetToModel = UpdateValueStrategy<Boolean?, Boolean?>(UpdateValueStrategy.POLICY_UPDATE)
                    val modelToTarget = UpdateValueStrategy<Boolean?, Boolean?>(UpdateValueStrategy.POLICY_UPDATE)
                    val bindInput = viewState.dbc.bindValue<Boolean, Boolean>(target, model, targetToModel, modelToTarget)
                }
                ApplicationData.ViewDef.datetime -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as DateTime
                    val inputProperty: DateTimeSelectionProperty = DateTimeSelectionProperty()
                    val target = inputProperty.observe(input)
                    val model = BeanProperties.value<T, LocalDate>(fieldName).observe(selectedItem)
                    val targetToModel = UpdateValueStrategy<String?, LocalDate?>(UpdateValueStrategy.POLICY_UPDATE)
                    val modelToTarget = UpdateValueStrategy<LocalDate?, String?>(UpdateValueStrategy.POLICY_UPDATE)
                    val bindInput = viewState.dbc.bindValue(target, model)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                }
                ApplicationData.ViewDef.lookup -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as ComboViewer
                    val comboSource = ApplicationData.lookups.getOrDefault(item[ApplicationData.ViewDef.lookupKey] as String, listOf())
                    val target: IObservableValue<LookupDetail> =
                            ViewerProperties.singleSelection<ComboViewer, LookupDetail>().observeDelayed(1, input)
                    val model = BeanProperties.value<T, String>(fieldName).observe(selectedItem)
                    val targetToModel = UpdateValueStrategy<LookupDetail, String>(UpdateValueStrategy.POLICY_UPDATE)
                    val modelToTarget = UpdateValueStrategy<String?, LookupDetail>(UpdateValueStrategy.POLICY_UPDATE)
                    targetToModel.setConverter(Converters.convertFromLookup)
                    modelToTarget.setConverter(Converters.convertToLookup(comboSource))
                    val bindInput = viewState.dbc.bindValue<LookupDetail, String?>(target, model, targetToModel, modelToTarget)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                }
                else -> {
                }
            }

            viewState.dbc.bindings.forEach {
                it.target.addChangeListener(viewState.listener)
            }

            val validationObserver = AggregateValidationStatus(viewState.dbc.bindings, AggregateValidationStatus.MAX_SEVERITY)
            val errorObservable: IObservableValue<String> = WidgetProperties.text<Label>().observe(
                    viewState.getWidgetFromViewState("lblErrors") as Label)
            val allValidationBinding: Binding = viewState.dbc.bindValue(errorObservable, validationObserver, null, null)


            //save button binding
            val btnSave = viewState.getWidgetFromViewState("btnSave") as Button
            val targetSave = WidgetProperties.enabled<Button>().observe(btnSave)
            val modelDirty = BeanProperties.value<DirtyFlag, Boolean>("dirty").observe(viewState.dirtyFlag)
            val bindSave = viewState.dbc.bindValue(targetSave, modelDirty)

        }
    }
}



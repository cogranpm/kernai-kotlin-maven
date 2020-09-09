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
import com.parinherm.databinding.*
import com.parinherm.databinding.Converters.bigDecimalValidator
import com.parinherm.entity.DirtyFlag
import com.parinherm.entity.IBeanDataEntity
import com.parinherm.entity.LookupDetail
import org.eclipse.core.databinding.AggregateValidationStatus
import org.eclipse.core.databinding.Binding
import org.eclipse.core.databinding.UpdateValueStrategy
import org.eclipse.core.databinding.ValidationStatusProvider
import org.eclipse.core.databinding.beans.typed.BeanProperties
import org.eclipse.core.databinding.conversion.text.NumberToStringConverter
import org.eclipse.core.databinding.conversion.text.StringToNumberConverter
import org.eclipse.core.databinding.observable.map.IObservableMap
import org.eclipse.core.databinding.observable.value.ComputedValue
import org.eclipse.core.databinding.observable.value.IObservableValue
import org.eclipse.core.databinding.property.value.IValueProperty
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport
import org.eclipse.jface.databinding.swt.ISWTObservableValue
import org.eclipse.jface.databinding.swt.typed.WidgetProperties
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider
import org.eclipse.jface.databinding.viewers.typed.ViewerProperties
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.jface.layout.TableColumnLayout
import org.eclipse.jface.viewers.*
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.custom.CTabItem
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.events.SelectionListener
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*
import java.math.BigDecimal
import java.time.LocalDate

object BeansViewBuilder {

    fun <T> renderView(parent: Composite, viewState: BeansViewState<T>, viewDefinition: Map<String, Any>): Composite where T : IBeanDataEntity {

        //val form: Map<String, Any> = ApplicationData.getView(viewId)
        val composite = Composite(parent, ApplicationData.swnone)
        val sashForm = SashForm(composite, SWT.BORDER)
        val listContainer = Composite(sashForm, ApplicationData.swnone)
        val editContainer = getEditContainer(sashForm, viewDefinition)

        val listView = TableViewer(listContainer, ApplicationData.listViewStyle)
        val listTable = listView.table
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
                }
                ApplicationData.ViewDef.float -> {
                    val input = Text(editContainer, ApplicationData.swnone)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input)
                    viewState.addWidgetToViewState(fieldName, input)

                    /* column observables */
                    val observableColumn: IValueProperty<T, Double> = BeanProperties.value<T, Double>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                }
                ApplicationData.ViewDef.money -> {
                    val input = Text(editContainer, ApplicationData.swnone)
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(input)
                    viewState.addWidgetToViewState(fieldName, input)

                    /* column observables */
                    val observableColumn: IValueProperty<T, BigDecimal> = BeanProperties.value<T, BigDecimal>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
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
                }
                ApplicationData.ViewDef.bool -> {
                    val input = Button(editContainer, SWT.CHECK)
                    GridDataFactory.fillDefaults().grab(false, false).applyTo(input)
                    viewState.addWidgetToViewState(fieldName, input)

                    /* column observables */
                    val observableColumn: IValueProperty<T, Boolean> = BeanProperties.value<T, Boolean>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
                }
                ApplicationData.ViewDef.datetime -> {
                    val input = DateTime(editContainer, SWT.DROP_DOWN or SWT.DATE)
                    GridDataFactory.fillDefaults().grab(false, false).applyTo(input)
                    viewState.addWidgetToViewState(fieldName, input)
                    /* column observables */
                    val observableColumn: IValueProperty<T, LocalDate> = BeanProperties.value<T, LocalDate>(fieldName)
                    columnLabelList.add(observableColumn.observeDetail(contentProvider.knownElements))
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
                val beanEntity = element as IBeanDataEntity
                return beanEntity?.getColumnValueByIndex(columnIndex)
                //return "${labelConverterList[columnIndex](element)}"
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
            viewState.currentItem = selectedItem as T
            createDataBindings(viewState, fields)
            Display.getDefault().timerExec(100) {
                viewState.selectingFlag = false
            }
        }

        btnSave.text = "Save"
        viewState.addWidgetToViewState("btnSave", btnSave)
        btnSave.enabled = false
        btnSave.addSelectionListener(SelectionListener.widgetSelectedAdapter { _ ->

            // needed if ApplicationData.defaultUpdatePolicy = UpdateValueStrategy.POLICY_ON_REQUEST
            //viewState.dbc.updateModels()

            viewState.dirtyFlag.dirty = false
            // debug the fields of the selected item

            if (viewState.currentItem?.id == 0L) {
                viewState.currentItem?.id = (viewState.wl.size + 1L)
                viewState.wl.add(viewState.currentItem)
                listView.selection = StructuredSelection(viewState.currentItem)
            }
            println(viewState.currentItem.toString())

        })

        btnNew.text = "New"
        btnNew.addSelectionListener(SelectionListener.widgetSelectedAdapter { _ ->
            // should probably just put ui into new mode
            val newItem = viewState.bean_maker()
            viewState.currentItem = newItem
            createDataBindings(viewState, fields)
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

    private fun hasChildViews(viewDefinition: Map<String, Any>): Boolean{
        if (viewDefinition.containsKey(ApplicationData.ViewDef.childViews)) {
            val childDefs = viewDefinition[ApplicationData.ViewDef.childViews] as List<Map<String, Any>>
            if (childDefs.size > 0) {
                return true
            }
        }
        return false
    }


    private fun getEditContainer(parent: Composite, viewDefinition: Map<String, Any>) : Composite {
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



    fun <T> createDataBindings(viewState: BeansViewState<T>, fields: List<Map<String, Any>>) where T: IBeanDataEntity {

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
                    val model = BeanProperties.value<T, String>(fieldName).observe(viewState.currentItem)
                    val modelToTarget = UpdateValueStrategy<String?, String?>(ApplicationData.defaultUpdatePolicy)
                    val targetToModel = UpdateValueStrategy<String?, String?>(ApplicationData.defaultUpdatePolicy)
                    if (item[ApplicationData.ViewDef.required] as Boolean) {
                        // all validations should be added to list and passed as a CompositeValidator which supports multiple
                        targetToModel.setAfterConvertValidator(CompositeValidator(listOf(RequiredValidation(fieldTitle))))
                    }
                    val bindInput = viewState.dbc.bindValue(target, model, targetToModel, modelToTarget)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)

                }
                ApplicationData.ViewDef.float -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as Text
                    val target = WidgetProperties.text<Text>(SWT.Modify).observe(input)
                    val model: IObservableValue<Double> = BeanProperties.value<T, Double>(fieldName).observe(viewState.currentItem)
                    val targetToModel = UpdateValueStrategy<String?, Double?>(ApplicationData.defaultUpdatePolicy)
                    val modelToTarget = UpdateValueStrategy<Double?, String?>(ApplicationData.defaultUpdatePolicy)
                    targetToModel.setConverter(StringToNumberConverter.toDouble(true))
                    targetToModel.setAfterGetValidator(FloatValidation(fieldTitle))
                    modelToTarget.setConverter(NumberToStringConverter.fromDouble(true))
                    val bindInput = viewState.dbc.bindValue<String, Double>(target, model, targetToModel, modelToTarget)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                }
                ApplicationData.ViewDef.money -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as Text
                    val target = WidgetProperties.text<Text>(SWT.Modify).observe(input)
                    val model: IObservableValue<BigDecimal> = BeanProperties.value<T, BigDecimal>(fieldName).observe(viewState.currentItem)
                    val targetToModel = UpdateValueStrategy<String?, BigDecimal?>(ApplicationData.defaultUpdatePolicy)
                    val modelToTarget = UpdateValueStrategy<BigDecimal?, String?>(ApplicationData.defaultUpdatePolicy)
                    targetToModel.setAfterGetValidator(bigDecimalValidator)
                    val bindInput = viewState.dbc.bindValue(target, model, targetToModel, modelToTarget)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                }
                ApplicationData.ViewDef.int -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as Spinner
                    val target = WidgetProperties.spinnerSelection().observe(input)
                    val model = BeanProperties.value<T, Int>(fieldName).observe(viewState.currentItem)
                    val targetToModel = UpdateValueStrategy<Int?, Int?>(ApplicationData.defaultUpdatePolicy)
                    val modelToTarget = UpdateValueStrategy<Int?, Int?>(ApplicationData.defaultUpdatePolicy)
                    val bindInput = viewState.dbc.bindValue<Int, Int>(target, model, targetToModel, modelToTarget)
                }
                ApplicationData.ViewDef.bool -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as Button
                    val target = WidgetProperties.buttonSelection().observe(input)
                    val model = BeanProperties.value<T, Boolean>(fieldName).observe(viewState.currentItem)
                    val targetToModel = UpdateValueStrategy<Boolean?, Boolean?>(ApplicationData.defaultUpdatePolicy)
                    val modelToTarget = UpdateValueStrategy<Boolean?, Boolean?>(ApplicationData.defaultUpdatePolicy)
                    val bindInput = viewState.dbc.bindValue<Boolean, Boolean>(target, model, targetToModel, modelToTarget)
                }
                ApplicationData.ViewDef.datetime -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as DateTime
                    val inputProperty: DateTimeSelectionProperty = DateTimeSelectionProperty()
                    val target = inputProperty.observe(input)
                    val model = BeanProperties.value<T, LocalDate>(fieldName).observe(viewState.currentItem)
                    val targetToModel = UpdateValueStrategy<String?, LocalDate?>(ApplicationData.defaultUpdatePolicy)
                    val modelToTarget = UpdateValueStrategy<LocalDate?, String?>(ApplicationData.defaultUpdatePolicy)
                    val bindInput = viewState.dbc.bindValue(target, model)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                }
                ApplicationData.ViewDef.lookup -> {
                    val input = viewState.getWidgetFromViewState(fieldName) as ComboViewer
                    val comboSource = ApplicationData.lookups.getOrDefault(item[ApplicationData.ViewDef.lookupKey] as String, listOf())
                    val target: IObservableValue<LookupDetail> =
                            ViewerProperties.singleSelection<ComboViewer, LookupDetail>().observeDelayed(1, input)
                    val model = BeanProperties.value<T, String>(fieldName).observe(viewState.currentItem)
                    val targetToModel = UpdateValueStrategy<LookupDetail, String>(ApplicationData.defaultUpdatePolicy)
                    val modelToTarget = UpdateValueStrategy<String?, LookupDetail>(ApplicationData.defaultUpdatePolicy)
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

            // ComputedValue is the critical piece in binding a single observable, say a button enabled
            // to multiple model properties, say a dirty flag or validation status
            val isValidationOk: IObservableValue<Boolean> = ComputedValue.create { validationObserver.value.isOK && modelDirty.value }
            val bindSave = viewState.dbc.bindValue(targetSave, isValidationOk)

            // needed if ApplicationData.defaultUpdatePolicy = UpdateValueStrategy.POLICY_ON_REQUEST
            //viewState.dbc.updateTargets()

        }
    }


}



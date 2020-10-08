/*
viewmodel needs to wrap the domain entity and expose the properties etc
it also contains a list of itself for lists
the view should bind to properties on this class if needed for stuff like
enabled state of buttons etc and bind to the domain entity wrapped by this class
the wrapping of the domain entity saves the trouble of doing proxies for every single
property on the domain object as we should do in a more enterprisey scenario
 */

package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.databinding.*
import com.parinherm.entity.*
import com.parinherm.entity.schema.BeanTestMapper
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.view.PersonView
import com.parinherm.view.View
import org.eclipse.core.databinding.*
import org.eclipse.core.databinding.beans.typed.BeanProperties
import org.eclipse.core.databinding.conversion.text.NumberToStringConverter
import org.eclipse.core.databinding.conversion.text.StringToNumberConverter
import org.eclipse.core.databinding.observable.ChangeEvent
import org.eclipse.core.databinding.observable.IChangeListener
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.core.databinding.observable.value.ComputedValue
import org.eclipse.core.databinding.observable.value.IObservableValue
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport
import org.eclipse.jface.databinding.swt.typed.WidgetProperties
import org.eclipse.jface.databinding.viewers.typed.ViewerProperties
import org.eclipse.jface.internal.databinding.swt.SWTObservableValueDecorator
import org.eclipse.jface.viewers.ComboViewer
import org.eclipse.jface.viewers.TableViewer
import org.eclipse.jface.viewers.TableViewerColumn
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.*
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.Comparator

class PersonViewModel(var person: Person) : ModelObject(), IBeanDataEntity, IFormViewModel {

    var selectingFlag = false
    val dbc = DataBindingContext()
    var dirtyFlag: DirtyFlag = DirtyFlag(false)
    var newFlag: NewFlag = NewFlag(false)
    val dataList = WritableList<PersonViewModel>()
    val comparator = Comparator()
    val entityNamePrefix = "person"

    // we have a reference to the view and control it's lifecycle
    // clients get to the view via this class
    var view: PersonView? = null

    // helper to do all the common stuff relating to viewmodel
    //var viewModel: FormViewModel<PersonViewModel>? = null


    init {
    }

    val stateChangeListener: IChangeListener = IChangeListener {
        processStateChange(it)
    }


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
        return when (source){
            is SWTObservableValueDecorator<*> -> true //source.widget != view.getSaveButton()
            else -> true
        }
    }



    override fun render(parent: CTabFolder): Composite {
        // view is instantiated
        //viewModel = FormViewModel(PersonView(parent))
        view = PersonView(parent, comparator)

        val data = BeanTestMapper.getAll(mapOf())
        // transform domain entities into view model instances
        val vmData = data.map { PersonViewModel(it) }

        dataList.clear()

        // populate the binding collection
        dataList.addAll(vmData)

        // should we call method on view passing data or just set the input directly?
        if (view != null) view!!.form.listView.input = dataList


        // implement all the event handlers on the view
        createCommands()

        return view!!.form.root
    }


    fun createCommands() {
        listSelectionCommand(view!!.form.listView)
        listHeaderSelection(view!!.form.listView)
    }


    fun listSelectionCommand(listView: TableViewer) {
        listView.addSelectionChangedListener { _ ->
            onListSelection()
        }
    }

    fun onListSelection() {
        selectingFlag = true
        val selection = view!!.form.listView.structuredSelection
        if (!selection.isEmpty) {
            val selectedItem = selection.firstElement
            val selectedViewModel = selectedItem as PersonViewModel
            person = selectedViewModel.person
            //modelBinder.createDataBindings(dbc, fields, selectedItem, this::getWidget, listener, dirtyFlag)
            makeFormBindings(entityNamePrefix)
            Display.getDefault().timerExec(100) {
                selectingFlag = false
            }
        }
    }

    fun listHeaderSelection(listView: TableViewer) {
        view!!.form.columns.forEachIndexed { index: Int, column: TableViewerColumn ->
            column.column.addSelectionListener(getSelectionAdapter(listView, column.column, index, comparator))
        }
    }

    /* this is a common function and should be moved out of here */
    fun getSelectionAdapter(viewer: TableViewer, column: TableColumn, index: Int, comparator: BeansViewerComparator): SelectionAdapter {
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


    override fun new() {
        TODO("Not yet implemented")
    }

    override fun save() {
        TODO("Not yet implemented")
    }

    // this is kind of a side effect
    override var id: Long
        get() = person.id
        set(value) {
            person.id = value
        }

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> person.name
            1 -> "${person.income}"
            2 -> "${person.height}"
            3 -> "${person.age}"
            4 -> {
                val listItem = ApplicationData.countryList.find { it.code == person.country }
                "${listItem?.label}"
            }
            5 -> "${person.enteredDate}"
            6 -> "${person.deceased}"
            else -> ""
        }
    }


    fun makeFormBindings(entityNamePrefix: String) {
        dbc.dispose()
        val bindings = dbc.validationStatusProviders
        for (binding: ValidationStatusProvider in bindings) {
            if (binding is Binding) {
                dbc.removeBinding(binding)
            }
        }

        val formBindings = view!!.form.formWidgets.map {
            val formWidget = it.value
            val fieldName = entityNamePrefix + "." + it.key
            val fieldType = formWidget.fieldType
            val binding = when (fieldType) {
                ApplicationData.ViewDef.text -> {
                    val target = WidgetProperties.text<Text>(SWT.Modify).observe(formWidget.control as Text)
                    val model = BeanProperties.value<PersonViewModel, String>(fieldName).observe(this)
                    val modelToTarget = UpdateValueStrategy<String?, String?>(ApplicationData.defaultUpdatePolicy)
                    val targetToModel = UpdateValueStrategy<String?, String?>(ApplicationData.defaultUpdatePolicy)
                    if (formWidget.fieldDef[ApplicationData.ViewDef.required] as Boolean) {
                        targetToModel.setAfterConvertValidator(CompositeValidator(listOf(RequiredValidation(formWidget.label.text))))
                    }
                    val bindInput = dbc.bindValue(target, model, targetToModel, modelToTarget)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                    bindInput
                }
                ApplicationData.ViewDef.float -> {
                    val target = WidgetProperties.text<Text>(SWT.Modify).observe(formWidget.control as Text)
                    val model: IObservableValue<Double> = BeanProperties.value<PersonViewModel, Double>(fieldName).observe(this)
                    val targetToModel = UpdateValueStrategy<String?, Double?>(ApplicationData.defaultUpdatePolicy)
                    val modelToTarget = UpdateValueStrategy<Double?, String?>(ApplicationData.defaultUpdatePolicy)
                    targetToModel.setConverter(StringToNumberConverter.toDouble(true))
                    targetToModel.setAfterGetValidator(FloatValidation(formWidget.label.text))
                    modelToTarget.setConverter(NumberToStringConverter.fromDouble(true))
                    val bindInput = dbc.bindValue<String, Double>(target, model, targetToModel, modelToTarget)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                    bindInput
                }
                ApplicationData.ViewDef.money -> {
                    val target = WidgetProperties.text<Text>(SWT.Modify).observe(formWidget.control as Text)
                    val model: IObservableValue<BigDecimal> = BeanProperties.value<PersonViewModel, BigDecimal>(fieldName).observe(this)
                    val targetToModel = UpdateValueStrategy<String?, BigDecimal?>(ApplicationData.defaultUpdatePolicy)
                    val modelToTarget = UpdateValueStrategy<BigDecimal?, String?>(ApplicationData.defaultUpdatePolicy)
                    targetToModel.setAfterGetValidator(Converters.bigDecimalValidator)
                    val bindInput = dbc.bindValue(target, model, targetToModel, modelToTarget)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                    bindInput
                }
                ApplicationData.ViewDef.int -> {
                    val target = WidgetProperties.spinnerSelection().observe(formWidget.control as Spinner)
                    val model = BeanProperties.value<PersonViewModel, Int>(fieldName).observe(this)
                    val targetToModel = UpdateValueStrategy<Int?, Int?>(ApplicationData.defaultUpdatePolicy)
                    val modelToTarget = UpdateValueStrategy<Int?, Int?>(ApplicationData.defaultUpdatePolicy)
                    val bindInput = dbc.bindValue<Int, Int>(target, model, targetToModel, modelToTarget)
                    bindInput
                }
                ApplicationData.ViewDef.bool -> {
                    val target = WidgetProperties.buttonSelection().observe(formWidget.control as Button)
                    val model = BeanProperties.value<PersonViewModel, Boolean>(fieldName).observe(this)
                    val targetToModel = UpdateValueStrategy<Boolean?, Boolean?>(ApplicationData.defaultUpdatePolicy)
                    val modelToTarget = UpdateValueStrategy<Boolean?, Boolean?>(ApplicationData.defaultUpdatePolicy)
                    val bindInput = dbc.bindValue<Boolean, Boolean>(target, model, targetToModel, modelToTarget)
                    bindInput
                }
                ApplicationData.ViewDef.datetime -> {
                    val inputProperty: DateTimeSelectionProperty = DateTimeSelectionProperty()
                    val target = inputProperty.observe(formWidget.control as DateTime)
                    val model = BeanProperties.value<PersonViewModel, LocalDate>(fieldName).observe(this)
                    val targetToModel = UpdateValueStrategy<String?, LocalDate?>(ApplicationData.defaultUpdatePolicy)
                    val modelToTarget = UpdateValueStrategy<LocalDate?, String?>(ApplicationData.defaultUpdatePolicy)
                    val bindInput = dbc.bindValue(target, model)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                    bindInput
                }
                /*
                ApplicationData.ViewDef.lookup -> {
                    val comboSource = ApplicationData.lookups.getOrDefault(formWidget.fieldDef[ApplicationData.ViewDef.lookupKey] as String, listOf())
                    val target: IObservableValue<LookupDetail> =
                            ViewerProperties.singleSelection<ComboViewer, LookupDetail>().observeDelayed(1, formWidget.control as ComboViewer)
                    val model = BeanProperties.value<PersonViewModel, String>(fieldName).observe(this)
                    val targetToModel = UpdateValueStrategy<LookupDetail, String>(ApplicationData.defaultUpdatePolicy)
                    val modelToTarget = UpdateValueStrategy<String?, LookupDetail>(ApplicationData.defaultUpdatePolicy)
                    targetToModel.setConverter(Converters.convertFromLookup)
                    modelToTarget.setConverter(Converters.convertToLookup(comboSource))
                    val bindInput = dbc.bindValue<LookupDetail, String?>(target, model, targetToModel, modelToTarget)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                    bindInput
                }
                 */
                else -> null
            }
            binding
        }

        dbc.bindings.forEach {
            it.target.addChangeListener(stateChangeListener)
        }

        val validationObserver = AggregateValidationStatus(dbc.bindings, AggregateValidationStatus.MAX_SEVERITY)
        val errorObservable: IObservableValue<String> = WidgetProperties.text<Label>().observe(view!!.form.lblErrors)
        val allValidationBinding: Binding = dbc.bindValue(errorObservable, validationObserver, null, null)


        /*
        fields.forEach { item: Map<String, Any> ->
            val fieldTitle = item[ApplicationData.ViewDef.title] as String
            val fieldName = item[ApplicationData.ViewDef.fieldName] as String
            when (item[ApplicationData.ViewDef.fieldDataType]) {
                ApplicationData.ViewDef.text -> {
                    val input = getWidget(fieldName) as Text
                    val target = WidgetProperties.text<Text>(SWT.Modify).observe(input)
                    val model = BeanProperties.value<T, String>(fieldName).observe(currentItem)
                    val modelToTarget = UpdateValueStrategy<String?, String?>(ApplicationData.defaultUpdatePolicy)
                    val targetToModel = UpdateValueStrategy<String?, String?>(ApplicationData.defaultUpdatePolicy)
                    if (item[ApplicationData.ViewDef.required] as Boolean) {
                        // all validations should be added to list and passed as a CompositeValidator which supports multiple
                        targetToModel.setAfterConvertValidator(CompositeValidator(listOf(RequiredValidation(fieldTitle))))
                    }
                    val bindInput = dbc.bindValue(target, model, targetToModel, modelToTarget)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)

                }
                ApplicationData.ViewDef.float -> {
                    val input = getWidget(fieldName) as Text
                    val target = WidgetProperties.text<Text>(SWT.Modify).observe(input)
                    val model: IObservableValue<Double> = BeanProperties.value<T, Double>(fieldName).observe(currentItem)
                    val targetToModel = UpdateValueStrategy<String?, Double?>(ApplicationData.defaultUpdatePolicy)
                    val modelToTarget = UpdateValueStrategy<Double?, String?>(ApplicationData.defaultUpdatePolicy)
                    targetToModel.setConverter(StringToNumberConverter.toDouble(true))
                    targetToModel.setAfterGetValidator(FloatValidation(fieldTitle))
                    modelToTarget.setConverter(NumberToStringConverter.fromDouble(true))
                    val bindInput = dbc.bindValue<String, Double>(target, model, targetToModel, modelToTarget)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                }
                ApplicationData.ViewDef.money -> {
                    val input = getWidget(fieldName) as Text
                    val target = WidgetProperties.text<Text>(SWT.Modify).observe(input)
                    val model: IObservableValue<BigDecimal> = BeanProperties.value<T, BigDecimal>(fieldName).observe(currentItem)
                    val targetToModel = UpdateValueStrategy<String?, BigDecimal?>(ApplicationData.defaultUpdatePolicy)
                    val modelToTarget = UpdateValueStrategy<BigDecimal?, String?>(ApplicationData.defaultUpdatePolicy)
                    targetToModel.setAfterGetValidator(Converters.bigDecimalValidator)
                    val bindInput = dbc.bindValue(target, model, targetToModel, modelToTarget)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                }
                ApplicationData.ViewDef.int -> {
                    val input = getWidget(fieldName) as Spinner
                    val target = WidgetProperties.spinnerSelection().observe(input)
                    val model = BeanProperties.value<T, Int>(fieldName).observe(currentItem)
                    val targetToModel = UpdateValueStrategy<Int?, Int?>(ApplicationData.defaultUpdatePolicy)
                    val modelToTarget = UpdateValueStrategy<Int?, Int?>(ApplicationData.defaultUpdatePolicy)
                    val bindInput = dbc.bindValue<Int, Int>(target, model, targetToModel, modelToTarget)
                }
                ApplicationData.ViewDef.bool -> {
                    val input = getWidget(fieldName) as Button
                    val target = WidgetProperties.buttonSelection().observe(input)
                    val model = BeanProperties.value<T, Boolean>(fieldName).observe(currentItem)
                    val targetToModel = UpdateValueStrategy<Boolean?, Boolean?>(ApplicationData.defaultUpdatePolicy)
                    val modelToTarget = UpdateValueStrategy<Boolean?, Boolean?>(ApplicationData.defaultUpdatePolicy)
                    val bindInput = dbc.bindValue<Boolean, Boolean>(target, model, targetToModel, modelToTarget)
                }
                ApplicationData.ViewDef.datetime -> {
                    val input = getWidget(fieldName) as DateTime
                    val inputProperty: DateTimeSelectionProperty = DateTimeSelectionProperty()
                    val target = inputProperty.observe(input)
                    val model = BeanProperties.value<T, LocalDate>(fieldName).observe(currentItem)
                    val targetToModel = UpdateValueStrategy<String?, LocalDate?>(ApplicationData.defaultUpdatePolicy)
                    val modelToTarget = UpdateValueStrategy<LocalDate?, String?>(ApplicationData.defaultUpdatePolicy)
                    val bindInput = dbc.bindValue(target, model)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                }
                ApplicationData.ViewDef.lookup -> {
                    val input = getWidget(fieldName) as ComboViewer
                    val comboSource = ApplicationData.lookups.getOrDefault(item[ApplicationData.ViewDef.lookupKey] as String, listOf())
                    val target: IObservableValue<LookupDetail> =
                            ViewerProperties.singleSelection<ComboViewer, LookupDetail>().observeDelayed(1, input)
                    val model = BeanProperties.value<T, String>(fieldName).observe(currentItem)
                    val targetToModel = UpdateValueStrategy<LookupDetail, String>(ApplicationData.defaultUpdatePolicy)
                    val modelToTarget = UpdateValueStrategy<String?, LookupDetail>(ApplicationData.defaultUpdatePolicy)
                    targetToModel.setConverter(Converters.convertFromLookup)
                    modelToTarget.setConverter(Converters.convertToLookup(comboSource))
                    val bindInput = dbc.bindValue<LookupDetail, String?>(target, model, targetToModel, modelToTarget)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                }
                else -> {
                }
            }

            dbc.bindings.forEach {
                it.target.addChangeListener(stateChangeListener)
            }

            val validationObserver = AggregateValidationStatus(dbc.bindings, AggregateValidationStatus.MAX_SEVERITY)
            val errorObservable: IObservableValue<String> = WidgetProperties.text<Label>().observe(
                    getWidget("lblErrors") as Label)
            val allValidationBinding: Binding = dbc.bindValue(errorObservable, validationObserver, null, null)


            //save button binding
            val btnSave = getWidget("btnSave") as Button
            val targetSave = WidgetProperties.enabled<Button>().observe(btnSave)
            val modelDirty = BeanProperties.value<DirtyFlag, Boolean>("dirty").observe(dirtyFlag)

            // ComputedValue is the critical piece in binding a single observable, say a button enabled
            // to multiple model properties, say a dirty flag or validation status
            val isValidationOk: IObservableValue<Boolean> = ComputedValue.create { validationObserver.value.isOK && modelDirty.value }
            val bindSave = dbc.bindValue(targetSave, isValidationOk)
*/
    }


    class Comparator : BeansViewerComparator(), IViewerComparator {

        val name_index = 0
        val income_index = 1
        val height_index = 2
        val age_index = 3
        val country_index = 4
        val enteredDate_index = 5
        val deceased_index = 6


        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as PersonViewModel
            val entity2 = e2 as PersonViewModel
            val rc = when (propertyIndex) {
                name_index -> entity1.person.name.compareTo(entity2.person.name)
                income_index -> entity1.person.income.compareTo(entity2.person.income)
                height_index -> entity1.person.height.compareTo(entity2.person.height)
                age_index -> entity1.person.age.compareTo(entity2.person.age)
                country_index -> compareLookups(entity1.person.country, entity2.person.country, ApplicationData.countryList)
                enteredDate_index -> entity1.person.enteredDate.compareTo(entity2.person.enteredDate)
                deceased_index -> entity1.person.deceased.compareTo(entity2.person.deceased)
                else -> 0
            }
            return flipSortDirection(rc)
        }

    }


}
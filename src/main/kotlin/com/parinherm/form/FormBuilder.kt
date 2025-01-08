/* functional style file with functions for
building a user interface

idea is to return values from functions that enable all the important
widgets to be exposed for further customization without using Maps, Keys and casting

functions should be pure wherever possible, ie side effect free, but this is hard with a gui

a view class will use functions to build up a form of widgets for display
 */

package com.parinherm.form

import com.parinherm.ApplicationData
import com.parinherm.databinding.*
import com.parinherm.entity.*
import com.parinherm.font.FontUtils
import com.parinherm.form.definitions.*
import com.parinherm.form.widgets.LookupPicker
import com.parinherm.form.widgets.SourceCodeViewer
import com.parinherm.form.widgets.ViewPicker
import com.parinherm.image.ImageUtils
import com.parinherm.lookups.LookupUtils
import com.parinherm.pickers.TopicPicker
import com.parinherm.view.filter.BaseViewFilter
import org.eclipse.core.databinding.*
import org.eclipse.core.databinding.beans.typed.BeanProperties
import org.eclipse.core.databinding.conversion.text.NumberToStringConverter
import org.eclipse.core.databinding.conversion.text.StringToNumberConverter
import org.eclipse.core.databinding.observable.IChangeListener
import org.eclipse.core.databinding.observable.map.IObservableMap
import org.eclipse.core.databinding.observable.set.IObservableSet
import org.eclipse.core.databinding.observable.value.ComputedValue
import org.eclipse.core.databinding.observable.value.IObservableValue
import org.eclipse.core.databinding.property.value.IValueProperty
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport
import org.eclipse.jface.databinding.swt.typed.WidgetProperties
import org.eclipse.jface.databinding.viewers.IViewerObservableValue
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider
import org.eclipse.jface.databinding.viewers.typed.ViewerProperties
import org.eclipse.jface.dialogs.MessageDialog
import org.eclipse.jface.layout.*
import org.eclipse.jface.viewers.*
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.custom.CTabItem
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.layout.RowLayout
import org.eclipse.swt.widgets.*
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate


fun getListFilters(parent: Composite, fields: List<FieldDef>): Map<String, Text> {
    val listFilters =
        fields.filter { isFieldTypeShownInLists(it.dataTypeDef) && it.filterable }
            .map {
                val filterBox = Text(parent, SWT.BORDER)
                filterBox.message = it.name
                RowDataFactory
                    .swtDefaults()
                    .hint(300, SWT.DEFAULT)
                    .applyTo(filterBox)
                it.name to filterBox
            }.toMap()
    return listFilters
}

fun getSearchButton(parent: Composite): Button {
    val button = Button(parent, SWT.PUSH)
    //button.text = "&Search"
    button.image = ImageUtils.getImage("folder-saved-search")
    RowDataFactory
        .swtDefaults()
        .applyTo(button)
    return button
}

fun getListViewer(
    parent: Composite,
    layout: TableColumnLayout,
    multipleSelect: Boolean = false
)
        : TableViewer {
    //create a composite to hold the viewer and the filters
    var style: Int
    if(multipleSelect){
       style = SWT.MULTI or SWT.H_SCROLL or SWT.V_SCROLL or SWT.FULL_SELECTION or SWT.BORDER
    } else {
        style = ApplicationData.listViewStyle
    }
    val listView = TableViewer(parent, style)
    val listTable = listView.table
    listTable.headerVisible = true
    listTable.linesVisible = true
    parent.layout = layout
    return listView
}

fun getSashForm(parent: Composite, viewDef: ViewDef): SashForm {
    var style: Int = ApplicationData.swnone
    style =
        if (viewDef.sashOrientation == SashOrientationDef.HORIZONTAL) {
            style or SWT.HORIZONTAL
        } else {
            style or SWT.VERTICAL
        }
    return SashForm(parent, style)
}

fun makeColumns(
    viewer: TableViewer,
    fields: List<FieldDef>,
    layout: TableColumnLayout
)
        : List<TableViewerColumn> {
    return fields
        .filter { isFieldTypeShownInLists(it.dataTypeDef) }
        .map { makeColumn(it, viewer, layout) }
}

fun isFieldTypeShownInLists(fieldType: DataTypeDef) =
    fieldType != DataTypeDef.SOURCE && fieldType != DataTypeDef.MEMO

fun makeColumn(
    fieldDef: FieldDef,
    viewer: TableViewer,
    layout: TableColumnLayout
): TableViewerColumn {
    val column = TableViewerColumn(viewer, SWT.LEFT)
    val col = column.column
    col.text = fieldDef.title
    col.resizable = true
    col.moveable = true
    layout.setColumnData(col, ColumnWeightData(100))
    return column
}

fun <E> makeViewerLabelProvider(
    fields: List<FieldDef>,
    knownElements: IObservableSet<E>
): ObservableMapLabelProvider where E : IBeanDataEntity {
    val observables = fields.map { makeColumnObservable(it, knownElements) }
    val labelMaps = observables.toTypedArray()
    val labelProvider = (object : ObservableMapLabelProvider(labelMaps) {
        override fun getColumnText(element: Any?, columnIndex: Int): String {
            val entity = element as IBeanDataEntity
            return entity?.getColumnValueByIndex(columnIndex) ?: ""
        }
    })
    return labelProvider
}


fun <E> makeColumnObservable(fieldDef: FieldDef, knownElements: IObservableSet<E>)
        : IObservableMap<E, out Any> where E : IBeanDataEntity {
    val fieldName = fieldDef.name
    val observableColumn: IValueProperty<E, out Any> =
        when (fieldDef.dataTypeDef) {
            DataTypeDef.TEXT -> BeanProperties.value<E, String>(fieldName)
            DataTypeDef.FLOAT -> BeanProperties.value<E, Double>(fieldName)
            DataTypeDef.MONEY -> BeanProperties.value<E, BigDecimal>(fieldName)
            DataTypeDef.INT -> BeanProperties.value<E, Int>(fieldName)
            DataTypeDef.BOOLEAN -> BeanProperties.value<E, Boolean>(fieldName)
            DataTypeDef.DATETIME -> BeanProperties.value<E, LocalDate>(fieldName)
            DataTypeDef.LOOKUP -> BeanProperties.value<E, String>(fieldName)
            DataTypeDef.REFERENCE -> BeanProperties.value<E, String>(fieldName)
            else -> BeanProperties.value<E, String>(fieldName)
        }
    return observableColumn.observeDetail(knownElements)
}

fun makeHeaderSection(parent: Composite): Composite {
    //add a toolbar above the fields
    val headerSection = Composite(parent, ApplicationData.swnone)
    headerSection.layout = GridLayout(1, true)
    GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(headerSection)
    return headerSection
}

fun makeHeaderText(headerSection: Composite, content: String) {
    val txtHeader = Text(headerSection, SWT.READ_ONLY)
    txtHeader.text = content
    val font = FontUtils.getFont(FontUtils.FONT_IBM_PLEX_MONO)
    txtHeader.font = font
    txtHeader.enabled = false
    GridDataFactory.fillDefaults().grab(true, false).applyTo(txtHeader)
}

fun makeToolbar(parent: Composite): Composite {
    //add a toolbar above the fields
    val toolbar = Composite(parent, ApplicationData.swnone)
    toolbar.layout = GridLayout(1, true)
    GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(toolbar)
    return toolbar
}

fun makeForm(fields: List<FieldDef>, parent: Composite)
        : Map<String, FormWidget> {
   // transform list of field definitions into  a map of widgets
    // with the fieldName as the key
    val fieldsMap = fields.sortedBy { it.sequence }.map {
        val fieldName = it.name
        val fieldType = it.dataTypeDef
        val label = makeInputLabel(parent, it.title)
        val control = makeInputWidget(
            parent,
            fieldName,
            fieldType,
            it
        )
        // returning a map entry for each iteration
        // generates a list of pairs
        fieldName to FormWidget(it, control)
    }.toMap()
    return fieldsMap
}

fun makeErrorLabel(parent: Composite): Label {
    val lblErrorsCaption = Label(parent, ApplicationData.labelStyle)
    lblErrorsCaption.text = "Problems"
    val lblErrors = Label(parent, ApplicationData.labelStyle)
    GridDataFactory.fillDefaults().applyTo(lblErrorsCaption)
    GridDataFactory.fillDefaults().applyTo(lblErrors)
    return lblErrors
}

fun makeInputLabel(parent: Composite, caption: String): Label {
    val label = Label(parent, ApplicationData.labelStyle)
    label.text = "$caption:"
    GridDataFactory.fillDefaults().applyTo(label)
    return label
}

fun getSizeHintValue(fieldDef: FieldDef): Int =
    when (fieldDef.sizeHint) {
        SizeDef.LARGE -> 20
        SizeDef.MEDIUM -> 10
        SizeDef.SMALL -> 5
    }

fun makeInputWidget(
    parent: Composite,
    fieldName: String,
    fieldType: DataTypeDef,
    fieldDef: FieldDef
): Any {

    val control = when (fieldType) {
        DataTypeDef.BLOB -> {
            val input = Text(parent, SWT.MULTI or SWT.BORDER or SWT.V_SCROLL or SWT.WRAP)
            input.setData("fieldName", fieldName)
            applyLayoutToField(input, true, true, getSizeHintValue(fieldDef) * input.lineHeight)
            input
        }
        DataTypeDef.TEXT -> {
            val input = Text(parent, SWT.BORDER)
            input.setData("fieldName", fieldName)
            //input.addListener(SWT.FocusOut) { input.selectAll() }
            input.addListener(SWT.FocusIn) { input.selectAll() }
            applyLayoutToField(input, true, false)
            input
        }
        DataTypeDef.MEMO -> {
            val input = Text(parent, SWT.MULTI or SWT.BORDER or SWT.V_SCROLL or SWT.WRAP)
            input.setData("fieldName", fieldName)
            applyLayoutToField(input, true, true, getSizeHintValue(fieldDef) * input.lineHeight)
            input
        }
        DataTypeDef.SOURCE -> {
            val input = SourceCodeViewer(parent)
            input.setData("fieldName", fieldName)
            applyLayoutToField(input.control, true, true, getSizeHintValue(fieldDef) * input.textWidget.lineHeight)
            input
        }
        DataTypeDef.FLOAT -> {
            val input = Text(parent, ApplicationData.swnone)
            input.addListener(SWT.FocusOut) { input.selectAll() }
            applyLayoutToField(input, true, false)
            input.setData("fieldName", fieldName)
            input
        }
        DataTypeDef.MONEY -> {
            val input = Text(parent, ApplicationData.swnone)
            input.addListener(SWT.FocusOut) { input.selectAll() }
            applyLayoutToField(input, true, false)
            input.setData("fieldName", fieldName)
            input
        }
        DataTypeDef.INT -> {
            val input = Spinner(parent, ApplicationData.swnone)
            input.minimum = Integer.MIN_VALUE
            input.maximum = Integer.MAX_VALUE
            applyLayoutToField(input, false, false)
            input.setData("fieldName", fieldName)
            input
        }
        DataTypeDef.BOOLEAN -> {
            val input = Button(parent, SWT.CHECK)
            applyLayoutToField(input, false, false)
            input.setData("fieldName", fieldName)
            input
        }
        DataTypeDef.DATE -> {
            val input = DateTime(parent, SWT.DROP_DOWN or SWT.DATE)
            applyLayoutToField(input, false, false)
            input.setData("fieldName", fieldName)
            input
        }
        DataTypeDef.DATETIME -> {
            val input = Text(parent, SWT.READ_ONLY)
            applyLayoutToField(input, true, false)
            input
        }
        DataTypeDef.TIME -> {
            val input = Text(parent, SWT.READ_ONLY)
            applyLayoutToField(input, true, false)
            input
        }
        DataTypeDef.LOOKUP -> {
            val input = ComboViewer(parent)
            GridDataFactory.fillDefaults().grab(true, false).applyTo(input.combo)
            input.contentProvider = ArrayContentProvider.getInstance()
            input.labelProvider = (object : LabelProvider() {
                override fun getText(element: Any): String {
                    return (element as LookupDetail).label
                }
            })
            val comboSource = LookupUtils.getLookupByKey(fieldDef.lookupKey as String, !fieldDef.required)
            applyLayoutToField(input.control, true, false)
            input.input = comboSource
            input.setData("fieldName", fieldName)
            input
        }
        DataTypeDef.REFERENCE -> {
            //makeLookupPicker(parent, fieldName)
            // this is temporory until I can add some capability to modeller
            if(fieldName.lowercase().equals("topicid")){
                TopicPicker.makePickerWidget(parent, fieldName)
            }
            else if (fieldName.lowercase().equals("lookupkey")){
                LookupPicker.makePickerWidget(parent, fieldName)
            }
            else {
                //LookupPicker.makePickerWidget(parent, fieldName)
                ViewPicker.makePickerWidget(parent, fieldName)
            }
        }
        DataTypeDef.FILE -> {
            val tmpContainer = Composite(parent, ApplicationData.swnone)
            tmpContainer.layout = GridLayout(2, false)
            val fileBtn = Button(tmpContainer, SWT.PUSH)
            val input = Text(tmpContainer, SWT.BORDER)
            fileBtn.image = ImageUtils.getImage("document-open")
            fileBtn.addSelectionListener(object : SelectionAdapter() {
                override fun widgetSelected(e: SelectionEvent?) {
                    val fileDialog = FileDialog(parent.shell, SWT.SINGLE or SWT.OPEN)
                    fileDialog.filterPath = "${ApplicationData.userPath}${ApplicationData.SCRIPT_PATH}"
                    val openResult = fileDialog.open()
                    if (openResult != null) {
                        input.text = "${fileDialog.filterPath}${File.separator}${fileDialog.fileName}"
                    }
                }
            })
            input.setData("fieldName", fieldName)
            applyLayoutToField(fileBtn, false, false)
            applyLayoutToField(input, true, false)
            applyLayoutToField(tmpContainer, true, false)
            input
        }
    }

    return control
}

fun <E> makeFormBindings(
    dbc: DataBindingContext,
    formWidgets: Map<String, FormWidget>,
    entity: E,
    lblErrors: Label,
    dirtyFlag: DirtyFlag,
    form: Form<*>,
    stateChangeListener: IChangeListener
): Map<String, Binding?> {
    dbc.dispose()
    val bindings = dbc.validationStatusProviders
    for (binding: ValidationStatusProvider in bindings) {
        if (binding is Binding) {
            dbc.removeBinding(binding)
        }
    }

    val formBindings = formWidgets.map {
        val formWidget = it.value
        val fieldName = it.key
        val fieldType = it.value.fieldDef.dataTypeDef
        val required = it.value.fieldDef.required
        fieldName to makeInputBinding(dbc, fieldType, fieldName, formWidget, entity, required)
        //makeInputBinding(dbc, fieldType, fieldName, formWidget, entity)
    }.toMap().toMutableMap()

    dbc.bindings.forEach {
        it.target.addChangeListener(stateChangeListener)
    }

    val validationObserver = AggregateValidationStatus(dbc.bindings, AggregateValidationStatus.MAX_SEVERITY)
    val errorObservable: IObservableValue<String> = WidgetProperties.text<Label>().observe(lblErrors)
    val allValidationBinding: Binding = dbc.bindValue(errorObservable, validationObserver, null, null)
    formBindings["validation"] = allValidationBinding


    /*********** save binding ************************/
    val targetSave = WidgetProperties.enabled<ToolItem>().observe(ApplicationData.getSaveToolbarButton())
    val modelDirty = BeanProperties.value<DirtyFlag, Boolean>("dirty").observe(dirtyFlag)
    val isValidationOk: IObservableValue<Boolean> =
        ComputedValue.create { validationObserver.value.isOK && modelDirty.value }
    val bindSave = dbc.bindValue(targetSave, isValidationOk)
    bindSave.target.addChangeListener() {
        ApplicationData.mainWindow.actionSave.isEnabled = ApplicationData.getSaveToolbarButton().enabled
    }

    /**************** delete binding *******************************/
    val toolDelete = ApplicationData.getDeleteToolbarButton()
    val deleteItemTarget = WidgetProperties.enabled<ToolItem>().observe(toolDelete)
    // cast to Viewer is to get rid of overload ambiguity from kotlin compiler
    val selectedEntity: IViewerObservableValue<E?> =
        ViewerProperties.singleSelection<TableViewer, E?>().observe(form.listView as Viewer)
    val isEntitySelected = ComputedValue.create { if (selectedEntity.value == null) false else true }
    //a binding that sets delete toolitem to disabled based on whether item in list is selected
    val bindDelete = dbc.bindValue(deleteItemTarget, isEntitySelected)

    // this is not firing, is supposed to update the Action based on ToolItem update
    // because a tool item affects just toolbar, and not the action which toolbar AND menu item are based on
    // in the iterim putting a forced enable of action in the change event of the tableviewer which is done in the FormViewModel base class
    bindDelete.target.addChangeListener {
        ApplicationData.mainWindow.actionDelete.isEnabled = ApplicationData.getDeleteToolbarButton().enabled
    }

    return formBindings
}

fun <E> makeInputBinding(
    dbc: DataBindingContext,
    fieldType: DataTypeDef,
    fieldName: String,
    formWidget: FormWidget,
    entity: E,
    required: Boolean
): Binding? {
    return when (fieldType) {
        DataTypeDef.MEMO, DataTypeDef.TEXT -> {
            val target = WidgetProperties.text<Text>(SWT.Modify).observe(formWidget.widget as Text)
            val model = BeanProperties.value<E, String>(fieldName).observe(entity)
            val modelToTarget = UpdateValueStrategy<String?, String?>(ApplicationData.defaultUpdatePolicy)
            val targetToModel = UpdateValueStrategy<String?, String?>(ApplicationData.defaultUpdatePolicy)
            if (formWidget.fieldDef.required) {
                targetToModel.setAfterConvertValidator(CompositeValidator(listOf(RequiredValidation(formWidget.fieldDef.title))))
            }
            val bindInput = dbc.bindValue(target, model, targetToModel, modelToTarget)
            ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
            bindInput
        }
        DataTypeDef.FLOAT -> {
            val target = WidgetProperties.text<Text>(SWT.Modify).observe(formWidget.widget as Text)
            val model: IObservableValue<Double> = BeanProperties.value<E, Double>(fieldName).observe(entity)
            val targetToModel = UpdateValueStrategy<String?, Double?>(ApplicationData.defaultUpdatePolicy)
            val modelToTarget = UpdateValueStrategy<Double?, String?>(ApplicationData.defaultUpdatePolicy)
            targetToModel.setConverter(StringToNumberConverter.toDouble(true))
            targetToModel.setAfterGetValidator(FloatValidation(formWidget.fieldDef.title))
            modelToTarget.setConverter(NumberToStringConverter.fromDouble(true))
            val bindInput = dbc.bindValue<String, Double>(target, model, targetToModel, modelToTarget)
            ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
            bindInput
        }
        DataTypeDef.MONEY -> {
            val target = WidgetProperties.text<Text>(SWT.Modify).observe(formWidget.widget as Text)
            val model: IObservableValue<BigDecimal> = BeanProperties.value<E, BigDecimal>(fieldName).observe(entity)
            val targetToModel = UpdateValueStrategy<String?, BigDecimal?>(ApplicationData.defaultUpdatePolicy)
            val modelToTarget = UpdateValueStrategy<BigDecimal?, String?>(ApplicationData.defaultUpdatePolicy)
            targetToModel.setAfterGetValidator(Converters.bigDecimalValidator)
            val bindInput = dbc.bindValue(target, model, targetToModel, modelToTarget)
            ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
            bindInput
        }
        DataTypeDef.INT -> {
            val target = WidgetProperties.spinnerSelection().observe(formWidget.widget as Spinner)
            val model = BeanProperties.value<E, Int>(fieldName).observe(entity)
            val targetToModel = UpdateValueStrategy<Int?, Int?>(ApplicationData.defaultUpdatePolicy)
            val modelToTarget = UpdateValueStrategy<Int?, Int?>(ApplicationData.defaultUpdatePolicy)
            val bindInput = dbc.bindValue<Int, Int>(target, model, targetToModel, modelToTarget)
            bindInput
        }
        DataTypeDef.BOOLEAN -> {
            val target = WidgetProperties.buttonSelection().observe(formWidget.widget as Button)
            val model = BeanProperties.value<E, Boolean>(fieldName).observe(entity)
            val targetToModel = UpdateValueStrategy<Boolean?, Boolean?>(ApplicationData.defaultUpdatePolicy)
            val modelToTarget = UpdateValueStrategy<Boolean?, Boolean?>(ApplicationData.defaultUpdatePolicy)
            val bindInput = dbc.bindValue<Boolean, Boolean>(target, model, targetToModel, modelToTarget)
            bindInput
        }
        DataTypeDef.DATE -> {
            val inputProperty: DateTimeSelectionProperty = DateTimeSelectionProperty()
            val target = inputProperty.observe(formWidget.widget as DateTime)
            val model = BeanProperties.value<E, LocalDate>(fieldName).observe(entity)
            val bindInput = dbc.bindValue(target, model)
            ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
            bindInput
        }
        DataTypeDef.DATETIME -> {
            val target = WidgetProperties.text<Text>(SWT.Modify).observe(formWidget.widget as Text)
            val model = BeanProperties.value<E, String>(fieldName).observe(entity)
            val bindInput = dbc.bindValue(target, model)
            ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
            bindInput
        }
        DataTypeDef.LOOKUP -> {
            val comboSource = LookupUtils.getLookupByKey(formWidget.fieldDef.lookupKey as String, !required)
            val target: IObservableValue<LookupDetail> =
                ViewerProperties.singleSelection<ComboViewer, LookupDetail>().observe(formWidget.widget as Viewer)
            val model = BeanProperties.value<E, String>(fieldName).observe(entity)
            val targetToModel = UpdateValueStrategy<LookupDetail, String>(ApplicationData.defaultUpdatePolicy)
            val modelToTarget = UpdateValueStrategy<String?, LookupDetail>(ApplicationData.defaultUpdatePolicy)
            targetToModel.setConverter(Converters.convertFromLookupDetail)
            modelToTarget.setConverter(Converters.convertToLookupDetail(comboSource))
            val bindInput = dbc.bindValue<LookupDetail, String?>(target, model, targetToModel, modelToTarget)
            ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
            bindInput
        }
        DataTypeDef.REFERENCE -> {
            //hardcoding this to a lookup picker at the moment
            if(fieldName.lowercase().equals("topicid")){
                val comboSource = TopicPicker.dataSource
                val target: IObservableValue<Topic> =
                    ViewerProperties.singleSelection<ComboViewer, Topic>().observe(formWidget.widget as Viewer)
                val model = BeanProperties.value<E, Long>(fieldName).observe(entity)
                val targetToModel = UpdateValueStrategy<Topic, Long>(ApplicationData.defaultUpdatePolicy)
                val modelToTarget = UpdateValueStrategy<Long, Topic>(ApplicationData.defaultUpdatePolicy)
                targetToModel.setConverter(TopicPicker.convertFrom)
                modelToTarget.setConverter(TopicPicker.convertTo(comboSource))
                val bindInput = dbc.bindValue(target, model, targetToModel, modelToTarget)
                ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                bindInput
            }
            else if (fieldName.lowercase().equals("lookupkey")){
                val comboSource = LookupPicker.dataSource
                val target: IObservableValue<Lookup> =
                    ViewerProperties.singleSelection<ComboViewer, Lookup>().observe(formWidget.widget as Viewer)
                val model = BeanProperties.value<E, String>(fieldName).observe(entity)
                val targetToModel = UpdateValueStrategy<Lookup, String>(ApplicationData.defaultUpdatePolicy)
                val modelToTarget = UpdateValueStrategy<String, Lookup>(ApplicationData.defaultUpdatePolicy)
                targetToModel.setConverter(Converters.convertFromLookup)
                modelToTarget.setConverter(Converters.convertToLookup(comboSource))
                val bindInput = dbc.bindValue<Lookup, String>(target, model, targetToModel, modelToTarget)
                ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                bindInput
            }
            else {
                val comboSource = ViewPicker.dataSource
                val target: IObservableValue<ViewDefinition> =
                    ViewerProperties.singleSelection<ComboViewer, ViewDefinition>().observe(formWidget.widget as Viewer)
                val model = BeanProperties.value<E, Long>(fieldName).observe(entity)
                val targetToModel = UpdateValueStrategy<ViewDefinition, Long>(ApplicationData.defaultUpdatePolicy)
                val modelToTarget = UpdateValueStrategy<Long, ViewDefinition>(ApplicationData.defaultUpdatePolicy)
                targetToModel.setConverter(ViewPicker.convertFrom)
                modelToTarget.setConverter(ViewPicker.convertTo(comboSource))
                val bindInput = dbc.bindValue(target, model, targetToModel, modelToTarget)
                ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                bindInput
                /*
                val comboSource = LookupPicker.dataSource
                val target: IObservableValue<Lookup> =
                    ViewerProperties.singleSelection<ComboViewer, Lookup>().observe(formWidget.widget as Viewer)
                val model = BeanProperties.value<E, String>(fieldName).observe(entity)
                val targetToModel = UpdateValueStrategy<Lookup, String>(ApplicationData.defaultUpdatePolicy)
                val modelToTarget = UpdateValueStrategy<String, Lookup>(ApplicationData.defaultUpdatePolicy)
                targetToModel.setConverter(Converters.convertFromLookup)
                modelToTarget.setConverter(Converters.convertToLookup(comboSource))
                val bindInput = dbc.bindValue<Lookup, String>(target, model, targetToModel, modelToTarget)
                ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                bindInput
                 */
            }
        }
        DataTypeDef.FILE -> {
            val target = WidgetProperties.text<Text>(SWT.Modify).observe(formWidget.widget as Text)
            val model = BeanProperties.value<E, String>(fieldName).observe(entity)
            val bindInput = dbc.bindValue(target, model)
            ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
            bindInput
        }

        else -> null
    }
}

fun applyLayoutToField(widget: Control, stretchH: Boolean, stretchY: Boolean, heightHint: Int? = null): Unit {
    var defaults = GridDataFactory.fillDefaults()
    defaults = defaults.grab(stretchH, stretchY)
    if (heightHint != null) {
        defaults.hint(SWT.DEFAULT, heightHint)
    }
    defaults.applyTo(widget)
}

fun makeEditContainer(hasChildViews: Boolean, parent: Composite): FormContainer {

    val editContainer = Composite(parent, ApplicationData.swnone)
    if (hasChildViews) {
        editContainer.layout = FillLayout(SWT.VERTICAL)
        val sashForm = SashForm(editContainer, SWT.BORDER or SWT.HORIZONTAL)
        val fieldsContainer = Composite(sashForm, ApplicationData.swnone)
        val childContainer = Composite(sashForm, ApplicationData.swnone)
        childContainer.layout = FillLayout(SWT.VERTICAL)
        //sashForm.weights = intArrayOf(1, 1)
        sashForm.setWeights(1, 1)
        sashForm.sashWidth = ApplicationData.defaultSashWidth
        fieldsContainer.layout = GridLayout(2, false)
        return FormContainer(fieldsContainer, childContainer)
    } else {
        editContainer.layout = GridLayout(2, false)
        return FormContainer(editContainer, null)
    }
}

fun makeEditContainer( parent: Composite): FormContainer {
    val editContainer = Composite(parent, ApplicationData.swnone)
    editContainer.layout = GridLayout(2, false)
    return FormContainer(editContainer, null)
}

fun makeChildFormContainer(parent: Composite, childDefs: List<ViewDef>): ChildFormContainer {
    val folder = CTabFolder(parent, SWT.TOP or SWT.BORDER)
    val childTabs = childDefs.mapIndexed { index: Int, item: ViewDef ->
        val childTab = makeChildTab(folder, item)
        if (index == 0) {
            folder.selection = childTab.tab
        }
        childTab
    }
    return ChildFormContainer(folder, childTabs)
}

fun getRightContainer(parent: SashForm?, hasChildViews: Boolean) : Composite? {
    if(hasChildViews){
        val container = Composite(parent!!, ApplicationData.swnone)
        container.layout = FillLayout()
        return container
    } else {
       return null
    }
}

fun getLeftContainer(sashParent: SashForm?, compositeParent: Composite?, hasChildViews: Boolean) : Composite {
    val leftContainer = if(hasChildViews) Composite(sashParent, ApplicationData.swnone) else Composite(compositeParent, ApplicationData.swnone)
    leftContainer.layout = FillLayout()
    return leftContainer
}

fun makeMainSash(parent: Composite, hasChildViews: Boolean) : SashForm? {
   return if(hasChildViews) SashForm(parent, SWT.HORIZONTAL) else null
}

fun makeFilterContainer(parent: Composite, filter: BaseViewFilter?) : Composite? {
    if (filter != null) {
        val filtersContainer = Composite(parent, ApplicationData.swnone)
        filtersContainer.layout = RowLayoutFactory
            .fillDefaults()
            .pack(true)
            .justify(false)
            .wrap(true)
            .center(true)
            .margins(3, 3)
            .create()
        GridDataFactory.defaultsFor(filtersContainer).grab(true, false).applyTo(filtersContainer)
        return filtersContainer
    } else {
        return null
    }
}

fun setupFilters(filtersContainer: Composite, filter: BaseViewFilter?, fields: List<FieldDef>, listView: TableViewer){
    val listFilters = getListFilters(filtersContainer, fields)
    filter!!.searchFields = listFilters
    listView.addFilter(filter)
    val searchButton: Button = getSearchButton(filtersContainer)
    searchButton?.addSelectionListener(widgetSelectedAdapter
    {
        listView.refresh()
    })
}

fun getGetChildForms(
    hasChildViews: Boolean,
    viewDefinition: ViewDef,
    formsContainer: FormContainer
): ChildFormContainer? {
    return if (hasChildViews) {
        makeChildFormContainer(formsContainer.childContainer!!, viewDefinition.childViews)
    } else {
        null
    }
}

fun getGetChildForms(
    hasChildViews: Boolean,
    viewDefinition: ViewDef,
    parent: Composite?
): ChildFormContainer? {
    return if (hasChildViews) {
        makeChildFormContainer(parent!!, viewDefinition.childViews)
    } else {
        null
    }
}


fun makeChildTab(folder: CTabFolder, childDefinition: ViewDef): ChildFormTab {
    val tab = CTabItem(folder, SWT.CLOSE)
    tab.text = childDefinition.title

    val childComposite = Composite(folder, ApplicationData.swnone)
    childComposite.layout = GridLayout()

    val buttonBar = Composite(childComposite, ApplicationData.swnone)

    val listComposite = Composite(childComposite, ApplicationData.swnone)
    val tableLayout = TableColumnLayout(true)

    buttonBar.layout = RowLayout()
    val btnAdd = Button(buttonBar, SWT.PUSH)
    //btnAdd.text = ViewDefConstants.add_caption
    btnAdd.image = ImageUtils.getImage("document-new")

    val btnRemove = Button(buttonBar, SWT.PUSH)
    btnRemove.image = ImageUtils.getImage("user-trash")
    //btnRemove.text = "Remove"

    tab.control = childComposite

    val fields = childDefinition.fieldDefinitions
    val listView = getListViewer(listComposite, tableLayout)
    val columns = makeColumns(listView, fields, tableLayout)

    GridLayoutFactory.fillDefaults().numColumns(1).margins(LayoutConstants.getMargins()).generateLayout(childComposite)
    GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonBar)
    GridDataFactory.fillDefaults().grab(true, true).applyTo(listComposite)
    val childKey = childDefinition.id
    return ChildFormTab(childKey, childDefinition, tab, buttonBar, btnAdd, btnRemove, listComposite, listView, columns)
}

fun hasChildViews(viewDefinition: ViewDef): Boolean = viewDefinition.childViews.isNotEmpty()

fun confirmDelete(): Boolean {
    return MessageDialog.openConfirm(Display.getDefault().activeShell, "Delete", "Delete, are you sure?")
}






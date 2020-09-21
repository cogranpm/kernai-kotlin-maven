package com.parinherm.builders

import com.parinherm.ApplicationData
import com.parinherm.databinding.*
import com.parinherm.entity.DirtyFlag
import com.parinherm.entity.LookupDetail
import org.eclipse.core.databinding.*
import org.eclipse.core.databinding.beans.typed.BeanProperties
import org.eclipse.core.databinding.conversion.text.NumberToStringConverter
import org.eclipse.core.databinding.conversion.text.StringToNumberConverter
import org.eclipse.core.databinding.observable.IChangeListener
import org.eclipse.core.databinding.observable.value.ComputedValue
import org.eclipse.core.databinding.observable.value.IObservableValue
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport
import org.eclipse.jface.databinding.swt.typed.WidgetProperties
import org.eclipse.jface.databinding.viewers.typed.ViewerProperties
import org.eclipse.jface.viewers.ComboViewer
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.*
import java.math.BigDecimal
import java.time.LocalDate

class ModelBinder <T> () {

    val dbc = DataBindingContext()

    private fun createDataBindings(fields: List<Map<String, Any>>, currentItem: T,
    getWidgetFromViewState: (String) -> Any, stateChangeListener: IChangeListener, dirtyFlag: DirtyFlag){
        dbc.dispose()
        val bindings = dbc.validationStatusProviders
        for (binding: ValidationStatusProvider in bindings) {
            if (binding is Binding) {
                dbc.removeBinding(binding)
            }
        }

        fields.forEach { item: Map<String, Any> ->
            val fieldTitle = item[ApplicationData.ViewDef.title] as String
            val fieldName = item[ApplicationData.ViewDef.fieldName] as String
            when (item[ApplicationData.ViewDef.fieldDataType]) {
                ApplicationData.ViewDef.text -> {
                    val input = getWidgetFromViewState(fieldName) as Text
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
                    val input = getWidgetFromViewState(fieldName) as Text
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
                    val input = getWidgetFromViewState(fieldName) as Text
                    val target = WidgetProperties.text<Text>(SWT.Modify).observe(input)
                    val model: IObservableValue<BigDecimal> = BeanProperties.value<T, BigDecimal>(fieldName).observe(currentItem)
                    val targetToModel = UpdateValueStrategy<String?, BigDecimal?>(ApplicationData.defaultUpdatePolicy)
                    val modelToTarget = UpdateValueStrategy<BigDecimal?, String?>(ApplicationData.defaultUpdatePolicy)
                    targetToModel.setAfterGetValidator(Converters.bigDecimalValidator)
                    val bindInput = dbc.bindValue(target, model, targetToModel, modelToTarget)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                }
                ApplicationData.ViewDef.int -> {
                    val input = getWidgetFromViewState(fieldName) as Spinner
                    val target = WidgetProperties.spinnerSelection().observe(input)
                    val model = BeanProperties.value<T, Int>(fieldName).observe(currentItem)
                    val targetToModel = UpdateValueStrategy<Int?, Int?>(ApplicationData.defaultUpdatePolicy)
                    val modelToTarget = UpdateValueStrategy<Int?, Int?>(ApplicationData.defaultUpdatePolicy)
                    val bindInput = dbc.bindValue<Int, Int>(target, model, targetToModel, modelToTarget)
                }
                ApplicationData.ViewDef.bool -> {
                    val input = getWidgetFromViewState(fieldName) as Button
                    val target = WidgetProperties.buttonSelection().observe(input)
                    val model = BeanProperties.value<T, Boolean>(fieldName).observe(currentItem)
                    val targetToModel = UpdateValueStrategy<Boolean?, Boolean?>(ApplicationData.defaultUpdatePolicy)
                    val modelToTarget = UpdateValueStrategy<Boolean?, Boolean?>(ApplicationData.defaultUpdatePolicy)
                    val bindInput = dbc.bindValue<Boolean, Boolean>(target, model, targetToModel, modelToTarget)
                }
                ApplicationData.ViewDef.datetime -> {
                    val input = getWidgetFromViewState(fieldName) as DateTime
                    val inputProperty: DateTimeSelectionProperty = DateTimeSelectionProperty()
                    val target = inputProperty.observe(input)
                    val model = BeanProperties.value<T, LocalDate>(fieldName).observe(currentItem)
                    val targetToModel = UpdateValueStrategy<String?, LocalDate?>(ApplicationData.defaultUpdatePolicy)
                    val modelToTarget = UpdateValueStrategy<LocalDate?, String?>(ApplicationData.defaultUpdatePolicy)
                    val bindInput = dbc.bindValue(target, model)
                    ControlDecorationSupport.create(bindInput, SWT.TOP or SWT.LEFT)
                }
                ApplicationData.ViewDef.lookup -> {
                    val input = getWidgetFromViewState(fieldName) as ComboViewer
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
                    getWidgetFromViewState("lblErrors") as Label)
            val allValidationBinding: Binding = dbc.bindValue(errorObservable, validationObserver, null, null)


            //save button binding
            val btnSave = getWidgetFromViewState("btnSave") as Button
            val targetSave = WidgetProperties.enabled<Button>().observe(btnSave)
            val modelDirty = BeanProperties.value<DirtyFlag, Boolean>("dirty").observe(dirtyFlag)

            // ComputedValue is the critical piece in binding a single observable, say a button enabled
            // to multiple model properties, say a dirty flag or validation status
            val isValidationOk: IObservableValue<Boolean> = ComputedValue.create { validationObserver.value.isOK && modelDirty.value }
            val bindSave = dbc.bindValue(targetSave, isValidationOk)

            // needed if ApplicationData.defaultUpdatePolicy = UpdateValueStrategy.POLICY_ON_REQUEST
            //dbc.updateTargets()

        }
    }

}
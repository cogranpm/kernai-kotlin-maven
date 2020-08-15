package com.parinherm.databinding

import com.parinherm.entity.LookupDetail
import org.eclipse.core.databinding.UpdateValueStrategy
import org.eclipse.core.databinding.conversion.text.NumberToStringConverter
import org.eclipse.core.databinding.conversion.text.StringToNumberConverter
import java.math.BigDecimal
import org.eclipse.core.databinding.conversion.Converter
import org.eclipse.core.databinding.conversion.IConverter
import org.eclipse.core.databinding.validation.ValidationStatus
import java.time.LocalDate
import java.time.LocalDateTime

import org.eclipse.jface.databinding.swt.WidgetValueProperty
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Widget
import org.eclipse.swt.widgets.DateTime
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.time.temporal.TemporalAccessor
import java.time.LocalTime
import java.time.temporal.ChronoField
import java.util.*


object Converters {

    val updToDouble = UpdateValueStrategy<String, Double>(UpdateValueStrategy.POLICY_UPDATE).setConverter(
        StringToNumberConverter.toDouble(true))
    val updFromDouble = UpdateValueStrategy<Double, String>(UpdateValueStrategy.POLICY_UPDATE).setConverter(
        NumberToStringConverter.fromDouble(true))

    val updToInt = UpdateValueStrategy<String, Int>(UpdateValueStrategy.POLICY_UPDATE).setConverter(StringToNumberConverter.toInteger(true))
    val updFromInt = UpdateValueStrategy<Int, String>(UpdateValueStrategy.POLICY_UPDATE).setConverter(NumberToStringConverter.fromInteger(true))

    val updToBigDecimal = UpdateValueStrategy<String, BigDecimal>(UpdateValueStrategy.POLICY_UPDATE).setConverter(StringToNumberConverter.toBigDecimal())
    val updFromBigDecimal = UpdateValueStrategy<BigDecimal, String>(UpdateValueStrategy.POLICY_UPDATE).setConverter(NumberToStringConverter.fromBigDecimal())

    val numberValidator = {x: Any ->
        val regex = "^[+-]?(\\d+(,\\d{3})*)$".toRegex()
        if(regex.matches(x.toString())){
            ValidationStatus.ok()
        }else {
            ValidationStatus.error("Invalid number entered")
        }
    }

    val floatValidator = {x: Any ->
        val regex = "[0-9]+(\\.){0,1}[0-9]*".toRegex()
        if(regex.matches(x.toString())){
            ValidationStatus.ok()
        }else {
            ValidationStatus.error("Invalid number entered")
        }
    }

    val bigDecimalValidator = {x: String ->
        try {
            val format = DecimalFormat("", DecimalFormatSymbols(Locale.ENGLISH))
                .parse(x)
            ValidationStatus.ok()
        } catch(e: ParseException){
            ValidationStatus.error("Invalid number entered")
        }
    }




    //converting from a combo lookup to a field type, say string
    val convertListItemDetail: IConverter = IConverter.create(LookupDetail::class.java, String::class. java,
    {
        o: LookupDetail => o?.code
    }
    )

    //converting from a field type to a lookup type
    //need to create a finder method to do it
    /*
    IConverter convertToListItemDetail = IConverter.create(String.class, ListItemDetail.class,
    { String o -> DataTypesList.findByCode(o)}
    )
    */

    init {
       updToInt.setAfterGetValidator(numberValidator)
       updToDouble.setAfterGetValidator(floatValidator)
       updToBigDecimal.setAfterGetValidator(bigDecimalValidator)
    }

}


class DateTimeSelectionProperty () : WidgetValueProperty<Widget, Any> (SWT.Selection) {

    val MONTH_MAPPING_VALUE = 1

    override fun getValueType() : Any {
        return TemporalAccessor::class.java as Any
    }

    override fun doGetValue(source: Widget) : Any {
        val dateTime: DateTime = source as DateTime
        if ((dateTime.style and SWT.TIME) != 0) {
            return LocalTime.of(dateTime.hours, dateTime.minutes, dateTime.seconds) as Any
        }
        return LocalDate.of(dateTime.year, dateTime.month, dateTime.day) as Any
    }


    override fun doSetValue(source: Widget, value: Any) {
        val dateTime: DateTime = source as DateTime
        val ta = getTemporalAccessor(value) ?: throw IllegalArgumentException()

        if((dateTime.style and SWT.TIME) != 0) {
            dateTime.setTime(ta.get(ChronoField.HOUR_OF_DAY),
                ta.get(ChronoField.MINUTE_OF_HOUR),
                ta.get(ChronoField.SECOND_OF_MINUTE))
        } else {
            dateTime.setDate(
                ta.get(ChronoField.YEAR),
                ta.get(ChronoField.MONTH_OF_YEAR) - MONTH_MAPPING_VALUE,
                ta.get(ChronoField.DAY_OF_MONTH)
            )
        }
    }

    private fun getTemporalAccessor(value: Any): TemporalAccessor? {
        if (value is Date) {
            return LocalDateTime.from((value as Date).toInstant())
        } else if(value is TemporalAccessor){
            return value as TemporalAccessor
        } else if(value is Calendar) {
            return LocalDateTime.from((value as Calendar).toInstant())
        }else {
            return null
        }
    }


}
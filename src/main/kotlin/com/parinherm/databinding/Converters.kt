package com.parinherm.databinding

import org.eclipse.core.databinding.UpdateValueStrategy
import org.eclipse.core.databinding.conversion.text.NumberToStringConverter
import org.eclipse.core.databinding.conversion.text.StringToNumberConverter
import java.math.BigDecimal

object Converters {

    val updToDouble = UpdateValueStrategy<String, Double>(UpdateValueStrategy.POLICY_UPDATE).setConverter(
        StringToNumberConverter.toDouble(true))
    val updFromDouble = UpdateValueStrategy<Double, String>(UpdateValueStrategy.POLICY_UPDATE).setConverter(
        NumberToStringConverter.fromDouble(true))

    val updToInt = UpdateValueStrategy<String, Int>(UpdateValueStrategy.POLICY_UPDATE).setConverter(StringToNumberConverter.toInteger(true))
    val updFromInt = UpdateValueStrategy<Int, String>(UpdateValueStrategy.POLICY_UPDATE).setConverter(NumberToStringConverter.fromInteger(true))

    val updToBigDecimal = UpdateValueStrategy<String, BigDecimal>(UpdateValueStrategy.POLICY_UPDATE).setConverter(StringToNumberConverter.toBigDecimal())
    val updFromBigDecimal = UpdateValueStrategy<BigDecimal, String>(UpdateValueStrategy.POLICY_UPDATE).setConverter(NumberToStringConverter.fromBigDecimal())

}
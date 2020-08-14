package com.parinherm.databinding

import org.eclipse.core.databinding.UpdateValueStrategy
import org.eclipse.core.databinding.conversion.text.NumberToStringConverter
import org.eclipse.core.databinding.conversion.text.StringToNumberConverter

object Converters {

    val updToDouble = UpdateValueStrategy<String, Double>(UpdateValueStrategy.POLICY_UPDATE).setConverter(
        StringToNumberConverter.toDouble(true))
    val updFromDouble = UpdateValueStrategy<Double, String>(UpdateValueStrategy.POLICY_UPDATE).setConverter(
        NumberToStringConverter.fromDouble(true))

}
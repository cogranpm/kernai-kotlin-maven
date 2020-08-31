package com.parinherm.databinding

import org.eclipse.core.databinding.validation.IValidator
import org.eclipse.core.databinding.validation.ValidationStatus
import org.eclipse.core.runtime.IStatus

class NumberValidation (val fieldTitle: String) : IValidator<String?> {
    override fun validate(value: String?): IStatus {
        val regex = "^[+-]?(\\d+(,\\d{3})*)$".toRegex()
        if(regex.matches(value.orEmpty())){
            return ValidationStatus.ok()
        }else {
            return ValidationStatus.error("Invalid $fieldTitle entered")
        }
    }
}
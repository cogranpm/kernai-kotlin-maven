package com.parinherm.databinding

import org.eclipse.core.databinding.validation.IValidator
import org.eclipse.core.databinding.validation.ValidationStatus
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.Status

class FloatValidation  (val fieldTitle: String) : IValidator<String?> {
    override fun validate(value: String?): IStatus {
        val regex = "[0-9]+(\\.){0,1}[0-9]*".toRegex()
        if (regex.matches(value.orEmpty())) {
            return ValidationStatus.ok()
        } else {
            return ValidationStatus.error("Invalid $fieldTitle entered")
        }
    }
}

package com.parinherm.databinding

import org.eclipse.core.databinding.validation.IValidator
import org.eclipse.core.databinding.validation.ValidationStatus
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.Status

class RequiredValidation (val fieldTitle: String) : IValidator<Any?> {
    override fun validate(value: Any?): IStatus {

        // potentially do the other types in this method
       if(value is String && value.isNullOrEmpty()) {
           return ValidationStatus.error("$fieldTitle is required")
       } else {
           return Status.OK_STATUS
       }

    }
}


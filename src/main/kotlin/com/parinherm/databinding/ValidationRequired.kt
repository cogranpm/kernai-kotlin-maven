package com.parinherm.databinding

import org.eclipse.core.databinding.validation.IValidator
import org.eclipse.core.databinding.validation.ValidationStatus
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.Status

class ValidationRequired (val fieldTitle: String) : IValidator<String?> {
    override fun validate(value: String?): IStatus {
       if(value.isNullOrEmpty()) {
           return ValidationStatus.error("$fieldTitle is required")
       } else {
           return Status.OK_STATUS
       }

    }
}
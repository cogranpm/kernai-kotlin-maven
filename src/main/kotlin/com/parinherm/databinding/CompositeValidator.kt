package com.parinherm.databinding

import org.eclipse.core.databinding.validation.IValidator
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.Status

class CompositeValidator (val validators: List<IValidator<Any?>>) : IValidator<Any?> {

    override fun validate(value: Any?): IStatus {
        validators.forEach {
            val status = it.validate(value)
            if(status.severity == IStatus.ERROR)
            {
                return status
            }
        }
        return Status.OK_STATUS
    }
}
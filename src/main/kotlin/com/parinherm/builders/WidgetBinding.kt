package com.parinherm.builders

import org.eclipse.core.databinding.UpdateValueStrategy
import org.eclipse.core.databinding.observable.value.IObservableValue
import org.eclipse.jface.databinding.swt.ISWTObservableValue


// in means ? super T
// out means ? extends T
data class WidgetBinding <T, M> (val bindingTarget: ISWTObservableValue<T>,
                                 val bindingModel: IObservableValue<M>,
                                 val targetToModel: UpdateValueStrategy<in T, out M>?,
                                 val modelToTarget: UpdateValueStrategy<in M, out T>?
)


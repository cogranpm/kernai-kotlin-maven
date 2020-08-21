package com.parinherm.builders

import org.eclipse.core.databinding.observable.value.IObservableValue
import org.eclipse.jface.databinding.swt.ISWTObservableValue

data class WidgetBinding<T> (val bindingTarget: ISWTObservableValue<T>, val bindingModel: IObservableValue<Any?>)

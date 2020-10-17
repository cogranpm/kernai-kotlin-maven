package com.parinherm.form

import com.parinherm.entity.IBeanDataEntity
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite

interface IForm <T> where T: IBeanDataEntity{

    fun refresh(list: WritableList<T>) : Unit
    val root : Composite
    fun setSelection(selection: StructuredSelection)
    // this needs more work, it's a toolbar now
    fun getSaveButton() : Button
    fun focusFirst()
    fun enable(flag: Boolean)
}
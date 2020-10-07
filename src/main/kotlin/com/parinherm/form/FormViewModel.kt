package com.parinherm.form

import com.parinherm.entity.DirtyFlag
import com.parinherm.entity.NewFlag
import com.parinherm.view.View
import org.eclipse.core.databinding.DataBindingContext
import org.eclipse.core.databinding.observable.ChangeEvent
import org.eclipse.core.databinding.observable.IChangeListener
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider
import org.eclipse.jface.internal.databinding.swt.SWTObservableValueDecorator

open class FormViewModel <T> (val view: View){
    val wl = WritableList<T>()
    var selectingFlag = false
    val dbc = DataBindingContext()
    var dirtyFlag: DirtyFlag = DirtyFlag(false)
    var newFlag: NewFlag = NewFlag(false)


    val dataList = WritableList<T>()
    /* this is needed because the new button
    needs to store an item before saving adding it to the
    writable list
     */
    var currentItem: T? = null

    init {

    }


    val listener: IChangeListener = IChangeListener {
        processStateChange(it)
    }

    private fun processStateChange(ce: ChangeEvent){
        if(isDirtyEventType(ce.source)) {

            //debugging stuff
            /****************************************
            val source = ce.source
            when (source) {
            is SWTObservableValueDecorator<*> -> println(source.widget)
            is SWTVetoableValueDecorator  -> {
            println(source.widget)
            }
            is ViewerObservableValueDecorator<*> -> {
            println(source.viewer)
            }
            }
             ************************************************/
            dirtyFlag.dirty = true
        }
    }

    private fun isDirtyEventType(source: Any): Boolean {
        /* clicking the save button triggers a state change on the dirty flag
        therefore this should not trigger the dirtyflag to become true (chicken and egg)
        also when changing items in the model list viewer, the state changes due to a
        new entity loading, this should be ignored as well
        hence the selecting flag in this class
        checkign the widget that is the source of the binding event is the best
        way i could figure out how to interrogate the source of data binding state change events
         */
        if (selectingFlag) return false
       return when (source){
            is SWTObservableValueDecorator<*> -> source.widget != view.getSaveButton()
            else -> true
        }
    }


}
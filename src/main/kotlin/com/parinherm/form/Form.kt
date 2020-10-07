/*
a base view class in the Model View ModelView paradigm
rules for the view are:
should wire up the events handlers to composable functions that are passed in
when they can be customized
actual behaviour of event handlers should not be defined here
databinding logic??? here or in the view model
the declarative stuff here perhaps and all else in viewmodel
ie - map widget to domain object????

 */

package com.parinherm.form

import com.parinherm.ApplicationData
import org.eclipse.core.databinding.DataBindingContext
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider
import org.eclipse.jface.layout.TableColumnLayout
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Composite


// view model type is the type argument
data class Form <T> (val parent: Composite, val viewDefinition: Map<String, Any>) {

    val fields = viewDefinition[ApplicationData.ViewDef.fields] as List<Map<String, Any>>
    val hasChildViews: Boolean = hasChildViews(viewDefinition)
    val root = Composite(parent, ApplicationData.swnone)
    val sashForm = SashForm(root, SWT.BORDER or SWT.VERTICAL)
    val listContainer = Composite(sashForm, ApplicationData.swnone)
    val tableLayout = TableColumnLayout(true)
    val listView = getListViewer(listContainer, tableLayout)
    val columns = makeColumns(listView, fields, tableLayout )
    val formsContainer = makeEditContainer(hasChildViews, sashForm)
    val lblErrors = makeErrorLabel(formsContainer.editContainer)
    val childFormsContainer: ChildFormContainer? = getGetChildForms(hasChildViews, viewDefinition, formsContainer)
    val formInputs = makeForm(fields, formsContainer.editContainer)
    val dbc = DataBindingContext()
    val contentProvider = ObservableListContentProvider<T>()

    init {
        //needs to be done after content is added
        sashForm.weights = intArrayOf(1, 2)
        sashForm.sashWidth = 4
        childFormsContainer?.childTabs?.forEach {println(it.key)}

        root.layout = FillLayout(SWT.VERTICAL)
        root.layout()

    }


}
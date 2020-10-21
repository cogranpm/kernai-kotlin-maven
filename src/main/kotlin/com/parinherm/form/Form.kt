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
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.IBeanDataEntity
import org.eclipse.core.databinding.DataBindingContext
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider
import org.eclipse.jface.layout.TableColumnLayout
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Control


// domain entity type is the type argument
// should really be the view-model

data class Form<T>(
    val parent: Composite,
    val viewDefinition: Map<String, Any>,
    val comparator: BeansViewerComparator
) : IForm<T> where T : IBeanDataEntity {

    val fields = viewDefinition[ApplicationData.ViewDef.fields] as List<Map<String, Any>>
    val hasChildViews: Boolean = hasChildViews(viewDefinition)
    override val root = Composite(parent, ApplicationData.swnone)
    val sashForm = SashForm(root, SWT.BORDER or SWT.VERTICAL)
    val listContainer = Composite(sashForm, ApplicationData.swnone)
    val tableLayout = TableColumnLayout(true)
    val listView = getListViewer(listContainer, tableLayout)
    val columns = makeColumns(listView, fields, tableLayout)
    val contentProvider = ObservableListContentProvider<T>()
    val formsContainer = makeEditContainer(hasChildViews, sashForm)
    val lblErrors = makeErrorLabel(formsContainer.editContainer)
    val btnDummySave = makeDummySaveButton(formsContainer.editContainer)
    val btnDummyDelete = makeDummyDeleteButton(formsContainer.editContainer)
    val childFormsContainer: ChildFormContainer? = getGetChildForms(hasChildViews, viewDefinition, formsContainer)
    val formWidgets = makeForm(fields, formsContainer.editContainer)
    val dbc = DataBindingContext()


    init {

        //needs to be done after content is added
        sashForm.weights = intArrayOf(1, 2)
        sashForm.sashWidth = 4
        //childFormsContainer?.childTabs?.forEach {println(it.key)}
        listView.contentProvider = contentProvider
        listView.labelProvider = makeViewerLabelProvider<T>(fields, contentProvider.knownElements)
        listView.comparator = comparator
        enable(false)

        root.layout = FillLayout(SWT.VERTICAL)
        root.layout()
    }

    override fun refresh(list: WritableList<T>) {
        listView.input = list
    }

    override fun setSelection(selection: StructuredSelection) {
        listView.selection = selection
    }

    override fun getSaveButton(): Button {
        return Button(null, SWT.NONE)
    }

    override fun focusFirst() {
        val firstWidget = formWidgets.values.first().widget
        if (firstWidget != null) {
            if (firstWidget is Control) {
                firstWidget.setFocus()
            } else if (firstWidget is Viewer) {
                firstWidget.control.setFocus()
            }
        }
    }

    override fun enable(flag: Boolean) {
        formWidgets.forEach {
            when (it.value.fieldType) {
                ApplicationData.ViewDef.text,
                ApplicationData.ViewDef.memo,
                ApplicationData.ViewDef.int,
                ApplicationData.ViewDef.float,
                ApplicationData.ViewDef.money,
                ApplicationData.ViewDef.bool,
                ApplicationData.ViewDef.datetime -> (it.value.widget as Control).enabled = flag
                ApplicationData.ViewDef.lookup -> (it.value.widget as Viewer).control.enabled = flag
            }
        }
    }


}
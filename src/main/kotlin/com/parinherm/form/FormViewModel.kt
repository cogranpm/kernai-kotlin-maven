package com.parinherm.form

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.DirtyFlag
import com.parinherm.entity.IBeanDataEntity
import com.parinherm.entity.NewFlag
import com.parinherm.entity.schema.IMapper
import com.parinherm.view.View
import org.eclipse.core.databinding.AggregateValidationStatus
import org.eclipse.core.databinding.DataBindingContext
import org.eclipse.core.databinding.beans.typed.BeanProperties
import org.eclipse.core.databinding.observable.ChangeEvent
import org.eclipse.core.databinding.observable.IChangeListener
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.core.databinding.observable.value.ComputedValue
import org.eclipse.core.databinding.observable.value.IObservableValue
import org.eclipse.jface.databinding.swt.typed.WidgetProperties
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider
import org.eclipse.jface.internal.databinding.swt.SWTObservableValueDecorator
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.jface.viewers.TableViewer
import org.eclipse.jface.viewers.TableViewerColumn
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.events.SelectionListener
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.TableColumn

abstract class FormViewModel<T>(val view: View<T>, val mapper: IMapper<T>, val entityMaker: () -> T) :
    IFormViewModel<T> where T : IBeanDataEntity {

    var selectingFlag = false
    val dbc = DataBindingContext()
    var dirtyFlag: DirtyFlag = DirtyFlag(false)
    var newFlag: NewFlag = NewFlag(false)
    val dataList = WritableList<T>()
    var currentEntity: T? = null

    /* if a viewmodel has a parent this will be called in the init
    to initialize the state according to what the parent passed in either
    a click on new or an edit of existing item
     */
    fun onLoad(selectedItem: T?) {
        if (selectedItem != null) {
            val itemInWritableList = dataList.find { it.id == selectedItem.id }
            if (itemInWritableList != null) {
                view.form.listView.setSelection(StructuredSelection(itemInWritableList), true)
                onListSelection()
            }
        } else {
            new()
        }
    }

    /* if viewmodel has a parent this is called after the save to update the
    child lists contained in the parent, the implementing viewmodel will
    trigger the call
     */
    fun afterSave(parentTabId: String?) {
        val tab = ApplicationData.tabs[parentTabId]
        if (tab != null) {
            if (!tab.isClosed) {
                tab.viewModel.refresh()
            }
        }
    }

    fun <E> wireChildTab(
        childFormTab: ChildFormTab,
        childTabId: String,
        comparator: BeansViewerComparator,
        input: WritableList<E>,
        childViewModelMaker: (E?) -> IFormViewModel<E>
    ) where E : IBeanDataEntity {
        val fields = childFormTab.childDefinition[ApplicationData.ViewDef.fields] as List<Map<String, Any>>
        val title = childFormTab.childDefinition[ApplicationData.ViewDef.title] as String

        val contentProvider = ObservableListContentProvider<E>()
        childFormTab.listView.contentProvider = contentProvider
        childFormTab.listView.labelProvider = makeViewerLabelProvider<E>(fields, contentProvider.knownElements)
        childFormTab.listView.comparator = comparator
        childFormTab.listView.input = input

        childFormTab.listView.addOpenListener {
            val selection = childFormTab.listView.structuredSelection
            val selectedItem = selection.firstElement
            val currentItem = selectedItem as E
            openTab(childViewModelMaker(currentItem), title, childTabId)
        }

        childFormTab.btnAdd.addSelectionListener(SelectionListener.widgetSelectedAdapter { _ ->
            openTab(childViewModelMaker(null), title, childTabId)
        })
        listHeaderSelection(childFormTab.listView, childFormTab.columns, comparator)
    }


    private fun <E> openTab(viewModel: IFormViewModel<E>, title: String, tabKey: String) where E : IBeanDataEntity {
        ApplicationData.makeTab(viewModel, title, tabKey)
    }

    private val stateChangeListener: IChangeListener = IChangeListener {
        processStateChange(it)
    }

    private fun processStateChange(ce: ChangeEvent) {
        if (isDirtyEventType(ce.source)) {

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
        return when (source) {
            is SWTObservableValueDecorator<*> -> true //source.widget != view.getSaveButton()
            else -> true
        }
    }

    override fun render(): Composite {
        createCommands()
        return view.form.root
    }

    override fun loadData(parameters: Map<String, Any>): Unit {
        dataList.clear()
        dataList.addAll(getData(parameters))
        view.form.refresh(dataList)
    }

    private fun createCommands() {
        listSelectionCommand(view.form.listView)
        listHeaderSelection(view.form.listView, view.form.columns, view.form.comparator)
    }


    private fun listSelectionCommand(listView: TableViewer) {
        listView.addSelectionChangedListener { _ ->
            onListSelection()
        }
    }

    protected fun onListSelection() {
        selectingFlag = true
        val selection = view.form.listView.structuredSelection
        if (!selection.isEmpty) {
            val selectedItem = selection.firstElement
            currentEntity = selectedItem as T
            changeSelection()
            Display.getDefault().timerExec(100) {
                selectingFlag = false
            }
        }
    }

    open fun changeSelection() {
        val formBindings = makeFormBindings(
            dbc,
            view.form.formWidgets,
            currentEntity,
            view.form.lblErrors,
            stateChangeListener
        )

        /* experiment with the dirty binding */
        val targetSave = WidgetProperties.enabled<Button>().observe(view.form.btnDummySave)
        val modelDirty = BeanProperties.value<DirtyFlag, Boolean>("dirty").observe(dirtyFlag)

        // ComputedValue is the critical piece in binding a single observable, say a button enabled
        // to multiple model properties, say a dirty flag or validation status
        val validationObserver = formBindings["validation"]?.model as AggregateValidationStatus
        val isValidationOk: IObservableValue<Boolean> = ComputedValue.create { validationObserver.value.isOK && modelDirty.value }
        val bindSave = dbc.bindValue(targetSave, isValidationOk)

        bindSave.target.addChangeListener() {
            //println("enabled of save has changed ${view.form.btnDummySave.enabled}")
            ApplicationData.mainWindow.actionSave.isEnabled = view.form.btnDummySave.enabled
        }

        view.form.enable(true)
    }

    fun listHeaderSelection(
        listView: TableViewer,
        columns: List<TableViewerColumn>,
        comparator: BeansViewerComparator
    ) {
        columns.forEachIndexed { index: Int, column: TableViewerColumn ->
            column.column.addSelectionListener(getSelectionAdapter(listView, column.column, index, comparator))
        }
    }

    private fun getSelectionAdapter(
        viewer: TableViewer,
        column: TableColumn,
        index: Int,
        comparator: BeansViewerComparator
    ): SelectionAdapter {
        return (object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                comparator.setColumn(index)
                val dir = comparator.getDirection()
                viewer.table.sortDirection = dir
                viewer.table.sortColumn = column
                viewer.refresh()
            }
        })
    }


    override fun save() {
        dirtyFlag.dirty = false
        if (currentEntity?.id == 0L) {
            mapper.save(currentEntity!!)
            dataList.add(currentEntity)
            view.form.setSelection(StructuredSelection(currentEntity))
        } else {
            if (currentEntity != null) {
                mapper.save(currentEntity!!)
            }
        }
    }

    override fun refresh() {
    }

    override fun getData(parameters: Map<String, Any>): List<T> {
        return mapper.getAll(mapOf())
    }

    override fun new() {
        currentEntity = entityMaker()
        changeSelection()
        view.form.focusFirst()
    }

    override fun delete() {
        if (confirmDelete()) {
            val selection = view.form.listView.structuredSelection
            if (!selection.isEmpty) {
                val selectedItem = selection.firstElement
                mapper.delete(selectedItem as T)
                dataList.remove(selectedItem as T)
                currentEntity == null
                changeSelection()
                view.form.enable(false)
            }
        }
    }

}
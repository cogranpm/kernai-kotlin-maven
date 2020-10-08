/*
viewmodel needs to wrap the domain entity and expose the properties etc
it also contains a list of itself for lists
the view should bind to properties on this class if needed for stuff like
enabled state of buttons etc and bind to the domain entity wrapped by this class
the wrapping of the domain entity saves the trouble of doing proxies for every single
property on the domain object as we should do in a more enterprisey scenario
 */

package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.*
import com.parinherm.entity.schema.PersonMapper
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.form.makeFormBindings
import com.parinherm.view.PersonView
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.jface.viewers.TableViewer
import org.eclipse.jface.viewers.TableViewerColumn
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.*
import java.math.BigDecimal
import java.time.LocalDate

class PersonViewModel(var person: Person) : FormViewModel<Person>(PersonMapper), IFormViewModel {

    val comparator = Comparator()
    val entityNamePrefix = "person"

    // we have a reference to the view and control it's lifecycle
    // clients get to the view via this class
    // wish this could be a val
    var view: PersonView? = null

    // helper to do all the common stuff relating to viewmodel
    // val viewModel: FormViewModel<PersonViewModel> = FormViewModel()


    init {
    }

    override fun render(parent: CTabFolder): Composite {
        // view is instantiated
        view = PersonView(parent, comparator)

        val data = mapper.getAll(mapOf())
        // transform domain entities into view model instances
        //val vmData = data.map { PersonViewModel(it) }

        dataList.clear()

        // populate the binding collection
        dataList.addAll(data)

        // should we call method on view passing data or just set the input directly?
        if (view != null) view!!.form.listView.input = dataList

        // implement all the event handlers on the view
        createCommands()

        return view!!.form.root
    }


    fun createCommands() {
        listSelectionCommand(view!!.form.listView)
        listHeaderSelection(view!!.form.listView)
    }


    fun listSelectionCommand(listView: TableViewer) {
        listView.addSelectionChangedListener { _ ->
            onListSelection()
        }
    }

    fun onListSelection() {
        selectingFlag = true
        val selection = view!!.form.listView.structuredSelection
        if (!selection.isEmpty) {
            val selectedItem = selection.firstElement
            person = selectedItem as Person
            changeSelection()
            Display.getDefault().timerExec(100) {
                selectingFlag = false
            }
        }
    }

    fun changeSelection() {
        val formBindings = makeFormBindings(dbc,
                entityNamePrefix,
                view!!.form.formWidgets,
                person,
                view!!.form.lblErrors,
                stateChangeListener)
    }

    fun listHeaderSelection(listView: TableViewer) {
        view!!.form.columns.forEachIndexed { index: Int, column: TableViewerColumn ->
            column.column.addSelectionListener(getSelectionAdapter(listView, column.column, index, comparator))
        }
    }

    /* this is a common function and should be moved out of here */
    fun getSelectionAdapter(viewer: TableViewer, column: TableColumn, index: Int, comparator: BeansViewerComparator): SelectionAdapter {
        val selectionAdapter = (object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                comparator.setColumn(index)
                val dir = comparator.getDirection()
                viewer.table.sortDirection = dir
                viewer.table.sortColumn = column
                viewer.refresh()
            }
        })
        return selectionAdapter
    }


    override fun new() {
        person = Person(0L, "", BigDecimal("0.0"), 6.70, 20, LocalDate.now(), "Aus", false)
        changeSelection()
    }

    override fun save() {
        dirtyFlag.dirty = false
        if (person?.id == 0L) {
            mapper.save(person)
            dataList.add(person)
            view!!.form.listView.selection = StructuredSelection(person)
        } else {
            if (person != null) {
                mapper.save(person)
            }
        }
    }

    /* this is kind of a side effect
    override var id: Long
        get() = person.id
        set(value) {
            person.id = value
        }

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> person.name
            1 -> "${person.income}"
            2 -> "${person.height}"
            3 -> "${person.age}"
            4 -> {
                val listItem = ApplicationData.countryList.find { it.code == person.country }
                "${listItem?.label}"
            }
            5 -> "${person.enteredDate}"
            6 -> "${person.deceased}"
            else -> ""
        }
    }

     */


    class Comparator : BeansViewerComparator(), IViewerComparator {

        val name_index = 0
        val income_index = 1
        val height_index = 2
        val age_index = 3
        val country_index = 4
        val enteredDate_index = 5
        val deceased_index = 6

        /*
        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as PersonViewModel
            val entity2 = e2 as PersonViewModel
            val rc = when (propertyIndex) {
                name_index -> entity1.person.name.compareTo(entity2.person.name)
                income_index -> entity1.person.income.compareTo(entity2.person.income)
                height_index -> entity1.person.height.compareTo(entity2.person.height)
                age_index -> entity1.person.age.compareTo(entity2.person.age)
                country_index -> compareLookups(entity1.person.country, entity2.person.country, ApplicationData.countryList)
                enteredDate_index -> entity1.person.enteredDate.compareTo(entity2.person.enteredDate)
                deceased_index -> entity1.person.deceased.compareTo(entity2.person.deceased)
                else -> 0
            }
            return flipSortDirection(rc)
        }

         */

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Person
            val entity2 = e2 as Person
            val rc = when (propertyIndex) {
                name_index -> entity1.name.compareTo(entity2.name)
                income_index -> entity1.income.compareTo(entity2.income)
                height_index -> entity1.height.compareTo(entity2.height)
                age_index -> entity1.age.compareTo(entity2.age)
                country_index -> compareLookups(entity1.country, entity2.country, ApplicationData.countryList)
                enteredDate_index -> entity1.enteredDate.compareTo(entity2.enteredDate)
                deceased_index -> entity1.deceased.compareTo(entity2.deceased)
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }


}
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
import com.parinherm.entity.DirtyFlag
import com.parinherm.entity.IBeanDataEntity
import com.parinherm.entity.NewFlag
import com.parinherm.entity.Person
import com.parinherm.entity.schema.BeanTestMapper
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.view.PersonView
import com.parinherm.view.View
import org.eclipse.core.databinding.DataBindingContext
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.widgets.Composite
import java.math.BigDecimal
import java.time.LocalDate

class PersonViewModel(val person: Person) : IBeanDataEntity, IFormViewModel {

    var selectingFlag = false
    val dbc = DataBindingContext()
    var dirtyFlag: DirtyFlag = DirtyFlag(false)
    var newFlag: NewFlag = NewFlag(false)
    val dataList = WritableList<PersonViewModel>()

    // we have a reference to the view and control it's lifecycle
    // clients get to the view via this class
    var view: PersonView? = null

    // helper to do all the common stuff relating to viewmodel
    //var viewModel: FormViewModel<PersonViewModel>? = null


    init {
    }

    override fun render(parent: CTabFolder) : Composite {
        // view is instantiated
        //viewModel = FormViewModel(PersonView(parent))
        view = PersonView(parent)

        val data = BeanTestMapper.getAll(mapOf())
        // transform domain entities into view model instances
        val vmData = data.map { PersonViewModel(it) }

        dataList.clear()

        // populate the binding collection
        dataList.addAll(vmData)

        // should we call method on view passing data or just set the input directly?
        if (view != null) view!!.form.listView.input = dataList
        return view!!.form.root
    }

    override fun new() {
        TODO("Not yet implemented")
    }

    override fun save() {
        TODO("Not yet implemented")
    }

    // this is kind of a side effect
    override var id: Long
        get() =  person.id
        set(value) { person.id = value}


    class Comparator : BeansViewerComparator(), IViewerComparator {

        val name_index = 0
        val income_index = 1
        val height_index = 2
        val age_index = 3
        val country_index = 4
        val enteredDate_index = 5
        val deceased_index = 6


        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as PersonViewModel
            val entity2 = e2 as PersonViewModel
            val rc = when(propertyIndex){
                name_index -> entity1.person.name.compareTo(entity2.person.name)
                income_index -> entity1.person.income.compareTo(entity2.person.income)
                height_index -> entity1.person.height.compareTo(entity2.person.height)
                age_index -> entity1.person.age.compareTo(entity2.person.age)
                country_index -> compareLookups(entity1.person.country, entity2.person.country, ApplicationData.countryList)
                enteredDate_index -> entity1.person.enteredDate.compareTo(entity2.person.enteredDate)
                deceased_index -> if(entity1.person.deceased == entity2.person.deceased) 0 else 1
                else -> 0
            }
            return flipSortDirection(rc)
        }

    }

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> person.name
            1 -> "${person.income}"
            2 -> "${person.height}"
            3 -> "${person.age}"
            4 -> {
                val listItem = ApplicationData.countryList.find { it.code == person.country}
                "${listItem?.label}"
            }
            5 -> "${person.enteredDate}"
            6 -> "${person.deceased}"
            else -> ""
        }
    }




}
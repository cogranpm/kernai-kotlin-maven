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
import com.parinherm.entity.Person
import com.parinherm.entity.PersonDetail
import com.parinherm.entity.schema.PersonDetailMapper
import com.parinherm.entity.schema.PersonMapper
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.lookups.LookupUtils
import com.parinherm.menus.TabInfo
import com.parinherm.view.PersonView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

class PersonViewModel(tabInfo: TabInfo) : FormViewModel<Person>
    (PersonView(tabInfo.folder, Comparator()), PersonMapper,
        {Person(0L, "", BigDecimal("0.0"), 6.70, 20, LocalDate.now(), "Aus", false)},
   tabInfo
) {

    val personDetails = WritableList<PersonDetail>()
    val personDetailComparator = PersonDetailViewModel.Comparator()

    init {
        if (view.form.childFormsContainer != null)
        {
            view.form.childFormsContainer!!.childTabs.forEach { childFormTab: ChildFormTab ->
                wireChildTab(childFormTab,
                    personDetailComparator,
                    personDetails,
                    ::makePersonDetailsViewModel,
                    PersonDetailMapper
                )
            }
        }
        createTab()
        loadData(mapOf())
    }

    private fun makePersonDetailsViewModel(currentChild: PersonDetail?) : IFormViewModel<PersonDetail> {
        return PersonDetailViewModel(
            currentEntity!!.id,
            currentChild,
            tabId,
            tabInfo.copy(caption = "Person Detail"))
    }

    override fun changeSelection(){
        val formBindings = super.changeSelection()
        /* specific to child list */
        personDetails.clear()
        personDetails.addAll(PersonDetailMapper.getAll(mapOf("personId" to currentEntity!!.id)))
    }

    override fun refresh() {
        super.refresh()
        personDetails.clear()
        personDetails.addAll(PersonDetailMapper.getAll(mapOf("personId" to currentEntity!!.id)))
    }

    class Comparator : BeansViewerComparator(), IViewerComparator {

        val name_index = 0
        val income_index = 1
        val height_index = 2
        val age_index = 3
        val country_index = 4
        val enteredDate_index = 5
        val deceased_index = 6


        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Person
            val entity2 = e2 as Person
            val rc = when (propertyIndex) {
                name_index -> compareString(entity1.name, entity2.name)
                income_index -> entity1.income.compareTo(entity2.income)
                height_index -> entity1.height.compareTo(entity2.height)
                age_index -> entity1.age.compareTo(entity2.age)
                country_index -> compareLookups(entity1.country, entity2.country, LookupUtils.getLookupByKey(LookupUtils.countryLookupKey, true))
                enteredDate_index -> entity1.enteredDate.compareTo(entity2.enteredDate)
                deceased_index -> entity1.deceased.compareTo(entity2.deceased)
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }

}
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
import com.parinherm.entity.schema.PersonDetailMapper
import com.parinherm.entity.schema.PersonMapper
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.makeViewerLabelProvider
import com.parinherm.view.PersonView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.events.SelectionListener
import java.math.BigDecimal
import java.time.LocalDate

class PersonViewModel(parent: CTabFolder) : FormViewModel<Person>(PersonView(parent, Comparator()), PersonMapper,
        {Person(0L, "", BigDecimal("0.0"), 6.70, 20, LocalDate.now(), "Aus", false)}) {


    val personDetails = WritableList<PersonDetail>()
    val personDetailComparator = PersonDetail.Comparator()
    val personDetailContentProvider = ObservableListContentProvider<PersonDetail>()


    init {
        if (view.form.childFormsContainer != null)
        {
            view.form.childFormsContainer!!.childTabs.forEach { childFormTab: ChildFormTab ->
                wireChildEntity(childFormTab)
                println(childFormTab.childDefinition)
                // set up the label provider for the child lists
            }
        }
        val data = mapper.getAll(mapOf())
        setData(data)
    }

    private fun wireChildEntity(childFormTab: ChildFormTab) : Unit {
        val fields = childFormTab.childDefinition[ApplicationData.ViewDef.fields] as List<Map<String, Any>>

        childFormTab.listView.contentProvider = personDetailContentProvider
        childFormTab.listView.labelProvider = makeViewerLabelProvider<PersonDetail>(fields, personDetailContentProvider.knownElements)
        childFormTab.listView.comparator = personDetailComparator
        childFormTab.listView.input = personDetails

        childFormTab.listView.addOpenListener {
            // open up a tab to edit child entity
            val selection = childFormTab.listView.structuredSelection
            val selectedItem = selection.firstElement
            // store the selected item in the list in the viewstate
            val currentPersonDetail = selectedItem as PersonDetail
            val viewModel = PersonDetailViewModel(currentEntity!!.id,
                    currentPersonDetail,
                    ApplicationData.mainWindow.folder)
            ApplicationData.makeTab(viewModel, "Person Details", ApplicationData.TAB_KEY_PERSONDETAIL)
            //val data = PersonDetailMapper.getAll(mapOf("personId" to currentEntity!!.id))

            /*
            val viewModel = PersonDetailViewModelOld(currentEntity!!.id,
                currentPersonDetail,
                ApplicationData.tabs[ApplicationData.TAB_KEY_PERSON],
                data,
                personDetailComparator,
                ModelBinder<PersonDetail>())

            ApplicationData.makeTab(viewModel, "Persons Detail", ApplicationData.TAB_KEY_PERSONDETAIL, ApplicationData.ViewDef.personDetailsViewId)

             */
        }

        childFormTab.btnAdd.addSelectionListener(SelectionListener.widgetSelectedAdapter { _ ->
            val data = PersonDetailMapper.getAll(mapOf("personId" to currentEntity!!.id))
            val viewModel = PersonDetailViewModel(currentEntity!!.id,
                    null,
                    ApplicationData.mainWindow.folder)
            ApplicationData.makeTab(viewModel, "Person Details", ApplicationData.TAB_KEY_PERSONDETAIL)
            /*
            val viewModel = PersonDetailViewModelOld(currentEntity!!.id, null,
                ApplicationData.tabs[ApplicationData.TAB_KEY_PERSON],
                data, personDetailComparator, ModelBinder<PersonDetail>())
            ApplicationData.makeTab(viewModel, "Persons Detail", ApplicationData.TAB_KEY_PERSONDETAIL, ApplicationData.ViewDef.personDetailsViewId)

             */
        })

        listHeaderSelection(childFormTab.listView, childFormTab.columns, personDetailComparator)
    }


    override fun changeSelection(){
        val formBindings = super.changeSelection()
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
/****************************
 *  is the view model class for the BeanTest entity
 *  manages the state for the view and all the events/commands and such
 *  state will consist of a list of BeanTest entities
 *  the current BeanTest entity, ie the one selected in the list
 *  ah ha, should really just manage this via a jface structured selection property
 *  that would be easier
  */

package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.ModelBinder
import com.parinherm.entity.BeanTest
import com.parinherm.entity.PersonDetail
import com.parinherm.tests.BeansBindingTestData
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.TableViewer
import org.eclipse.jface.viewers.TableViewerColumn
import org.eclipse.swt.custom.CTabItem
import org.eclipse.swt.events.SelectionListener
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite


class BeanTestViewModel(data: List<BeanTest>, bean_maker: ()-> BeanTest, comparator: BeansViewerComparator, modelBinder: ModelBinder<BeanTest>)
    : ViewModel<BeanTest>(data, bean_maker, comparator, modelBinder){

   val personDetails = WritableList<PersonDetail>()
    val personDetailComparator = PersonDetail.Comparator()

   override  fun render(parent: Composite, viewDefinition: Map<String, Any>): Composite {
       val composite = super.render(parent, viewDefinition)
       val childDefs = viewDefinition[ApplicationData.ViewDef.childViews] as List<Map<String, Any>>
       // each child in turn wire up the lists and handlers
       wireChildren(childDefs)
       return composite
    }

    fun wireChildren(childDefs: List<Map<String, Any>>) : Unit {

        /* do all the child entities here */

        val personDetailViewDef = childDefs.find {  it[ApplicationData.ViewDef.viewid] == ApplicationData.ViewDef.personDetailsViewId  }
        if (personDetailViewDef != null) {
            wirePersonDetail(personDetailViewDef)

        }
    }

    private fun wirePersonDetail(viewDef: Map<String, Any>) : Unit {
        val fields = viewDef[ApplicationData.ViewDef.fields] as List<Map<String, Any>>

        val personDetailWidgetsMap: Map<String, Any> = getWidget(ApplicationData.ViewDef.personDetailsViewId) as Map<String, Any>
        val btnAdd: Button = personDetailWidgetsMap[ApplicationData.ViewDef.btnAdd] as Button
        val btnRemove: Button = personDetailWidgetsMap[ApplicationData.ViewDef.btnRemove] as Button
        val tab: CTabItem = personDetailWidgetsMap[ApplicationData.ViewDef.tab] as CTabItem
        val list: TableViewer = personDetailWidgetsMap[ApplicationData.ViewDef.list] as TableViewer

        createListViewBindings<PersonDetail>(list, fields, personDetailComparator)

        // open and double click are same thing
        /* enter on an item generates the events
        list.addDoubleClickListener {
            println("double clicked an item")
        }
         */

        list.addOpenListener {
            // open up a tab to edit child entity
            val selection = list.structuredSelection
            val selectedItem = selection.firstElement
            // store the selected item in the list in the viewstate
            val currentPersonDetail = selectedItem as PersonDetail
            println(currentPersonDetail)
        }

        btnAdd.addSelectionListener(SelectionListener.widgetSelectedAdapter { _ ->
            /* what is best source of truth for the parent
            is it the selection on the parent list or
            the value cached in the viewmodel (this instance)
             */
            println("new person detail clicked, parent is $currentItem")
            val newPersonDetail = PersonDetail(0, "", currentItem!!.id, "")
            val data = BeansBindingTestData.personDetails.filter { it.beanTestId == currentItem!!.id }
            val viewModel = PersonDetailViewModel(currentItem!!.id, data, PersonDetail.Factory::make, personDetailComparator, ModelBinder<PersonDetail>())
            ApplicationData.makeTab(viewModel, "Person Detail", ApplicationData.TAB_KEY_PERSONDETAIL, ApplicationData.ViewDef.personDetailsViewId)
        })

        listHeaderSelection(list, fields, personDetailComparator)
        /*fields.forEachIndexed {index: Int, item: Map<String, Any> ->
            val fieldName = item[ApplicationData.ViewDef.fieldName] as String
            val column = widgets[ApplicationData.ViewDef.makeColumnMapKey(fieldName)] as TableViewerColumn
            column.column.addSelectionListener(getSelectionAdapter(list, column.column, index, PersonDetail.Comparator()))
        }

         */
    }

    override fun afterListSelection(listView: TableViewer, currentItem: BeanTest) {
        personDetails.clear()
        personDetails.addAll(BeansBindingTestData.personDetails.filter { it.beanTestId == currentItem.id })
        val personDetailWidgetsMap: Map<String, Any> = getWidget(ApplicationData.ViewDef.personDetailsViewId) as Map<String, Any>
        val list: TableViewer = personDetailWidgetsMap["list"] as TableViewer
        list.input = personDetails

    }


}

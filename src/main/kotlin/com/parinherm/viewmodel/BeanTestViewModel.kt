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
import com.parinherm.builders.ViewModel
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.ModelBinder
import com.parinherm.builders.ViewBuilder
import com.parinherm.entity.BeanTest
import com.parinherm.entity.PersonDetail
import com.parinherm.tests.BeansBindingTestData
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.TableViewer
import org.eclipse.swt.custom.CTabItem
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.TabItem
import sun.jvm.hotspot.debugger.win32.coff.TestDebugInfo


class BeanTestViewModel(data: List<BeanTest>, bean_maker: ()-> BeanTest, comparator: BeansViewerComparator, modelBinder: ModelBinder<BeanTest>)
    : ViewModel<BeanTest> (data, bean_maker, comparator, modelBinder){

   val personDetails = WritableList<PersonDetail>()

   override  fun render(parent: Composite, viewDefinition: Map<String, Any>): Composite {
        val composite = super.render(parent, viewDefinition)
       // get the widgets map
       // this is a map of widgets
       val personDetailWidgetsMap: Map<String, Any> = getWidget(ApplicationData.ViewDef.personDetailsViewId) as Map<String, Any>
       val btnAdd: Button = personDetailWidgetsMap["btnAdd"] as Button
       val tab: CTabItem = personDetailWidgetsMap["tab"] as CTabItem
       val list: TableViewer = personDetailWidgetsMap["list"] as TableViewer

       val childDefs = viewDefinition[ApplicationData.ViewDef.childViews] as List<Map<String, Any>>
       // each child in turn wire up the lists and handlers
       val personDetailViewDef = childDefs.find {  it[ApplicationData.ViewDef.viewid] == ApplicationData.ViewDef.personDetailsViewId  }
       if (personDetailViewDef != null) {
           val fields = personDetailViewDef[ApplicationData.ViewDef.fields] as List<Map<String, Any>>
           createListViewBindings<PersonDetail>(list, fields, PersonDetail.Comparator())
       }
        return composite
    }

    override fun afterListSelection(listView: TableViewer, currentItem: BeanTest) {
        personDetails.clear()
        personDetails.addAll(BeansBindingTestData.personDetails.filter { it.beanTestId == currentItem.id })
        val personDetailWidgetsMap: Map<String, Any> = getWidget(ApplicationData.ViewDef.personDetailsViewId) as Map<String, Any>
        val list: TableViewer = personDetailWidgetsMap["list"] as TableViewer
        list.input = personDetails

    }


}

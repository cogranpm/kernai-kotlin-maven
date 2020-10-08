/****************************
 *  is the view model class for the BeanTest entity
 *  manages the state for the view and all the events/commands and such
 *  state will consist of a list of BeanTest entities
 *  the current BeanTest entity, ie the one selected in the list
 *  ah ha, should really just manage this via a jface structured selection property
 *  that would be easier
  */

package com.parinherm.viewmodel


/*

class BeanTestViewModel(data: List<Person>, comparator: BeansViewerComparator, modelBinder: ModelBinder<Person>)
    : ViewModel<Person>(data, comparator, modelBinder, PersonMapper){

   val personDetails = WritableList<PersonDetail>()
    val personDetailComparator = PersonDetail.Comparator()

   override fun render(parent: Composite, viewDefinition: Map<String, Any>): Composite {
       val composite = super.render(parent, viewDefinition)
       val childDefs = viewDefinition[ApplicationData.ViewDef.childViews] as List<Map<String, Any>>
       // each child in turn wire up the lists and handlers
       wireChildren(childDefs)
       return composite
    }

    override fun makeNewEntity(): Person {
        return Person(0, "", BigDecimal(0), 0.0, 0, LocalDate.now(), "Aus", false)
    }


    fun wireChildren(childDefs: List<Map<String, Any>>) : Unit {

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

        list.addOpenListener {
            // open up a tab to edit child entity
            val selection = list.structuredSelection
            val selectedItem = selection.firstElement
            // store the selected item in the list in the viewstate
            val currentPersonDetail = selectedItem as PersonDetail
            val data = PersonDetailMapper.getAll(mapOf("beanTestId" to currentItem!!.id))
            val viewModel = PersonDetailViewModel(currentItem!!.id,
                currentPersonDetail,
                ApplicationData.tabs[ApplicationData.TAB_KEY_DATA_BINDING_TEST],
                data,
                personDetailComparator,
                ModelBinder<PersonDetail>())
            ApplicationData.makeTab(viewModel, "Person Detail", ApplicationData.TAB_KEY_PERSONDETAIL, ApplicationData.ViewDef.personDetailsViewId)
        }

        btnAdd.addSelectionListener(SelectionListener.widgetSelectedAdapter { _ ->
           val data = PersonDetailMapper.getAll(mapOf("beanTestId" to currentItem!!.id))
            val viewModel = PersonDetailViewModel(currentItem!!.id, null,
                    ApplicationData.tabs[ApplicationData.TAB_KEY_DATA_BINDING_TEST],
                    data, personDetailComparator, ModelBinder<PersonDetail>())
            ApplicationData.makeTab(viewModel, "Person Detail", ApplicationData.TAB_KEY_PERSONDETAIL, ApplicationData.ViewDef.personDetailsViewId)
        })

        listHeaderSelection(list, fields, personDetailComparator)
   }

    override fun afterListSelection(listView: TableViewer, currentItem: Person) {
        personDetails.clear()
        //personDetails.addAll(BeansBindingTestData.personDetails.filter { it.beanTestId == currentItem.id })
        personDetails.addAll(PersonDetailMapper.getAll(mapOf("beanTestId" to currentItem.id)))
        val personDetailWidgetsMap: Map<String, Any> = getWidget(ApplicationData.ViewDef.personDetailsViewId) as Map<String, Any>
        val list: TableViewer = personDetailWidgetsMap["list"] as TableViewer
        list.input = personDetails

    }



}


 */

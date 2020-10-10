package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.PersonDetail
import com.parinherm.entity.schema.PersonDetailMapper
import com.parinherm.form.FormViewModel
import com.parinherm.view.PersonDetailView
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder

class PersonDetailViewModel (val personId: Long, val selectedPersonDetail: PersonDetail?, val openedFromTabId: String?,  parent: CTabFolder) :
        FormViewModel<PersonDetail>(PersonDetailView(parent, Comparator()),
        PersonDetailMapper, {PersonDetail(0, "", personId, ApplicationData.speciesList[0].code)}) {


    init {
        val data = mapper.getAll(mapOf("personId" to personId))
        setData(data)
        if (selectedPersonDetail!= null) {
            val itemInWritableList = dataList.find { it.id == selectedPersonDetail.id }
            if (itemInWritableList != null) {
                view.form.listView.setSelection(StructuredSelection(itemInWritableList), true)
                onListSelection()
            }
        } else {
            new()
        }

    }

    override fun save() {
        super.save()
        val tab = ApplicationData.tabs[openedFromTabId]
        if (tab != null) {
            if (!tab.isClosed){
                tab.viewModel.refresh()
            }
        }
    }


    class Comparator : BeansViewerComparator(), IViewerComparator {

        val nickname_index = 0
        val petSpecies_index = 1

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as PersonDetail
            val entity2 = e2 as PersonDetail
            val rc = when(propertyIndex){
                nickname_index -> entity1.nickname.compareTo(entity2.nickname)
                petSpecies_index -> compareLookups(entity1.petSpecies, entity2.petSpecies, ApplicationData.speciesList)
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }

}
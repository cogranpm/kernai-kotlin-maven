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
        loadData(mapOf("personId" to personId))
        onLoad(selectedPersonDetail)
    }

    override fun getData(parameters: Map<String, Any>): List<PersonDetail> {
        return mapper.getAll(parameters as Map<String, Long>)
    }

    override fun save() {
        super.save()
        afterSave(openedFromTabId)
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
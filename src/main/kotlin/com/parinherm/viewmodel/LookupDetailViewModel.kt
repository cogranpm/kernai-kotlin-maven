package com.parinherm.viewmodel

import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.LookupDetail
import com.parinherm.entity.schema.LookupDetailMapper
import com.parinherm.form.FormViewModel
import com.parinherm.view.LookupDetailView
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder

class LookupDetailViewModel (val lookupId: Long, val selectedLookupDetail: LookupDetail?, val openedFromTabId: String?, parent: CTabFolder) :
    FormViewModel<LookupDetail>(
        LookupDetailView(parent, Comparator()),
        LookupDetailMapper, { LookupDetail.make( lookupId) }) {

    init {
        loadData(mapOf("lookupId" to lookupId))
        onLoad(selectedLookupDetail)
    }

    override fun getData(parameters: Map<String, Any>): List<LookupDetail> {
        return mapper.getAll(parameters as Map<String, Long>)
    }

    override fun save() {
        super.save()
        afterSave(openedFromTabId)
    }


    class Comparator : BeansViewerComparator(), IViewerComparator {

        private val codeIndex = 0
        private val labelIndex = 1

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as LookupDetail
            val entity2 = e2 as LookupDetail
            val rc = when(propertyIndex){
                codeIndex -> entity1.code.compareTo(entity2.code)
                labelIndex -> entity1.label.compareTo(entity2.label)
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }

}
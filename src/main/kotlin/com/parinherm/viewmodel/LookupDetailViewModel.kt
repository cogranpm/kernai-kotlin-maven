package com.parinherm.viewmodel

import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.Lookup
import com.parinherm.entity.LookupDetail
import com.parinherm.entity.schema.LookupDetailMapper
import com.parinherm.form.FormViewModel
import com.parinherm.form.makeHeaderText
import com.parinherm.lookups.LookupUtils
import com.parinherm.menus.TabInfo
import com.parinherm.view.LookupDetailView
import org.eclipse.jface.viewers.Viewer
import org.jasypt.util.text.AES256TextEncryptor

class LookupDetailViewModel (
    val lookup: Lookup,
    val selectedLookupDetail: LookupDetail?,
    val openedFromTabId: String?,
    tabInfo: TabInfo) :
    FormViewModel<LookupDetail>(
        LookupDetailView(tabInfo.folder, Comparator()),
        LookupDetailMapper, { LookupDetail.make( lookup.id) }, tabInfo) {

    init {
        loadData(mapOf("lookupId" to lookup.id))
        onLoad(selectedLookupDetail)
        val changeWarning = "Note that if the Code value is changed, the data for all Forms that use this Lookup Code must also be re-saved manually."
        makeHeaderText(this.view.form.headerSection, "Lookup: ${lookup.label}. $changeWarning")
    }

    override fun getData(parameters: Map<String, Any>): List<LookupDetail> {
        return mapper.getAll(parameters as Map<String, Long>)
    }

    override fun save() {
       super.save()
        afterSave(openedFromTabId)

        // refresh the global lookup "cache"
        if(currentEntity != null) {
            currentEntity?.lookupId?.let { LookupUtils.reloadLookup(it) }
        }
    }


    class Comparator : BeansViewerComparator(), IViewerComparator {

        private val codeIndex = 0
        private val labelIndex = 1

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as LookupDetail
            val entity2 = e2 as LookupDetail
            val rc = when(propertyIndex){
                codeIndex -> compareString(entity1.code, entity2.code)
                labelIndex -> compareString(entity1.label, entity2.label)
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }

}
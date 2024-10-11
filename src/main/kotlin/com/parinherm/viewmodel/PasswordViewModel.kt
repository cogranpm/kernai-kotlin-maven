package com.parinherm.viewmodel

import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.LookupDetail
import com.parinherm.entity.schema.LookupDetailMapper
import com.parinherm.form.FormViewModel
import com.parinherm.lookups.LookupUtils
import com.parinherm.menus.TabInfo
import com.parinherm.view.PasswordView
import org.eclipse.jface.viewers.Viewer

class PasswordViewModel( tabInfo: TabInfo, val lookupId: Long) : FormViewModel<LookupDetail>(
    PasswordView(tabInfo.folder, PasswordComparator()),
    LookupDetailMapper,
    { LookupDetail.make(lookupId) },
    tabInfo
) {

    init {
        createTab()
        loadData(mapOf("lookupId" to lookupId))
    }

    override fun getData(parameters: Map<String, Any>): List<LookupDetail> {
        return LookupDetailMapper.getAll(mapOf("lookupId" to lookupId))
    }

    override fun save() {
        super.save()
        // refresh the global lookup "cache"
        if(currentEntity != null) {
            currentEntity?.lookupId?.let { LookupUtils.reloadLookup(it) }
        }
    }


    class PasswordComparator : BeansViewerComparator(), IViewerComparator {

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as LookupDetail
            val entity2 = e2 as LookupDetail
            val rc = when (propertyIndex) {

                else -> 0
            }
            return flipSortDirection(rc)
        }
    }

}

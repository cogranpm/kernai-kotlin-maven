package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.Lookup
import com.parinherm.entity.LookupDetail
import com.parinherm.entity.schema.LookupDetailMapper
import com.parinherm.entity.schema.LookupMapper
import com.parinherm.entity.schema.PersonDetailMapper
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.view.LookupView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder

class LookupViewModel(parent: CTabFolder) : FormViewModel<Lookup>(
    LookupView(parent, Comparator()),
    LookupMapper, { Lookup.make() }
) {

    private val lookupDetails = WritableList<LookupDetail>()
    val lookupDetailsComparator = LookupDetailViewModel.Comparator()

    init {
        if (view.form.childFormsContainer != null) {
            view.form.childFormsContainer!!.childTabs.forEach { childFormTab: ChildFormTab ->
                wireChildTab(
                    childFormTab,
                    ApplicationData.TAB_KEY_LOOKUPDETAIL,
                    lookupDetailsComparator,
                    lookupDetails,
                    ::makeLookupDetailsViewModel
                )
            }
        }
        loadData(mapOf())
    }

    private fun makeLookupDetailsViewModel(currentChild: LookupDetail?): IFormViewModel<LookupDetail> {
        return LookupDetailViewModel(
            currentEntity!!.id,
            currentChild,
            ApplicationData.TAB_KEY_LOOKUP,
            ApplicationData.mainWindow.folder
        )
    }

    override fun changeSelection() {
        val formBindings = super.changeSelection()
        /* specific to child list */
        lookupDetails.clear()
        lookupDetails.addAll(LookupDetailMapper.getAll(mapOf("lookupId" to currentEntity!!.id)))
    }

    override fun refresh() {
        super.refresh()
        lookupDetails.clear()
        lookupDetails.addAll(LookupDetailMapper.getAll(mapOf("lookupId" to currentEntity!!.id)))
    }


    class Comparator : BeansViewerComparator(), IViewerComparator {

        val key_index = 0
        val label_index = 1

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Lookup
            val entity2 = e2 as Lookup
            val rc = when (propertyIndex) {
                key_index -> compareString(entity1.key, entity2.key)
                label_index -> compareString(entity1.label, entity2.label)
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }

}
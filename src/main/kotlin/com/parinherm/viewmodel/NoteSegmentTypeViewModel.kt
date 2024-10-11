package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.*
import com.parinherm.entity.schema.*
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.menus.TabInfo
import com.parinherm.view.NoteSegmentTypeView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder

class NoteSegmentTypeViewModel(
    val noteSegmentTypeHeaderId: Long,
    val selectedNoteSegmentType: NoteSegmentType?,
    val openedFromTabId: String?,
    tabInfo: TabInfo) : FormViewModel<NoteSegmentType>(
    NoteSegmentTypeView(tabInfo.folder, Comparator()),
    NoteSegmentTypeMapper,
    { NoteSegmentType.make() }, tabInfo) {

    init {
        loadData(mapOf("noteSegmentTypeHeaderId" to noteSegmentTypeHeaderId))
        onLoad(selectedNoteSegmentType)
    }

    override fun getData(parameters: Map<String, Any>): List<NoteSegmentType> {
        return mapper.getAll(parameters as Map<String, Long>)
    }

    override fun save() {
        super.save()
        afterSave(openedFromTabId)
    }

    class Comparator : BeansViewerComparator(), IViewerComparator {


        val title_index = 0

        val fontDesc_index = 1

        val createdDate_index = 2


        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as NoteSegmentType
            val entity2 = e2 as NoteSegmentType
            val rc = when (propertyIndex) {

                title_index -> compareString(entity1.title, entity2.title)

                fontDesc_index -> compareString(entity1.fontDesc, entity2.fontDesc)

                createdDate_index -> entity1.createdDate.compareTo(entity2.createdDate)

                else -> 0
            }
            return flipSortDirection(rc)
        }
    }


}
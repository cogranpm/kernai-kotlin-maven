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
import com.parinherm.view.NoteSegmentView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder

class NoteSegmentViewModel(
    val noteId: Long,
    val selectedNoteSegment: NoteSegment?,
    val openedFromTabId: String?,
    tabInfo: TabInfo) : FormViewModel<NoteSegment>(
    NoteSegmentView(tabInfo.folder, Comparator()),
    NoteSegmentMapper,
    { NoteSegment.make(noteId) }, tabInfo) {

    init {
        loadData(mapOf("noteId" to noteId))
        onLoad(selectedNoteSegment)
    }

    override fun getData(parameters: Map<String, Any>): List<NoteSegment> {
        return mapper.getAll(parameters as Map<String, Long>)
    }

    override fun save() {
        super.save()
        afterSave(openedFromTabId)
    }

    class Comparator : BeansViewerComparator(), IViewerComparator {


        val noteSegmentTypeId_index = 0

        val title_index = 1

        val body_index = 2

        val bodyFile_index = 3

        val createdDate_index = 4


        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as NoteSegment
            val entity2 = e2 as NoteSegment
            val rc = when (propertyIndex) {

                noteSegmentTypeId_index -> entity1.noteSegmentTypeId.compareTo(entity2.noteSegmentTypeId)

                title_index -> compareString(entity1.title, entity2.title)

                body_index -> compareString(entity1.body, entity2.body)

                bodyFile_index -> compareString(entity1.bodyFile, entity2.bodyFile)

                createdDate_index -> entity1.createdDate.compareTo(entity2.createdDate)

                else -> 0
            }
            return flipSortDirection(rc)
        }
    }


}
package com.parinherm.viewmodel

import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.Ingredient
import com.parinherm.entity.NoteDetail
import com.parinherm.entity.schema.NoteDetailMapper
import com.parinherm.form.FormViewModel
import com.parinherm.view.NoteDetailView
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder

class NoteDetailViewModel(val noteHeaderId: Long, val selectedNoteDetail: NoteDetail?, val openedFromTabId: String?, parent: CTabFolder) : FormViewModel<NoteDetail>(
    NoteDetailView(parent, Comparator()),
    NoteDetailMapper, { NoteDetail.make(noteHeaderId) })
{

    init {
        loadData(mapOf("noteHeaderId" to noteHeaderId))
        if (selectedNoteDetail != null) {
            val itemInWritableList = dataList.find { it.id == selectedNoteDetail.id }
            if (itemInWritableList != null) {
                view.form.listView.setSelection(StructuredSelection(itemInWritableList), true)
                onListSelection()
            }
        } else {
            new()
        }

    }

    override fun getData(parameters: Map<String, Any>): List<NoteDetail> {
        return mapper.getAll(parameters as Map<String, Long>)
    }


    class Comparator : BeansViewerComparator(), IViewerComparator {
        val name_index = 0

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as NoteDetail
            val entity2 = e2 as NoteDetail
            val rc = when (propertyIndex) {
                name_index -> entity1.name.toLowerCase().compareTo(entity2.name.toLowerCase())
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }

}
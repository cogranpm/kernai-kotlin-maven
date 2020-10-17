package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.NoteDetail
import com.parinherm.entity.NoteHeader
import com.parinherm.entity.schema.NoteDetailMapper
import com.parinherm.entity.schema.NoteHeaderMapper
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.form.makeViewerLabelProvider
import com.parinherm.view.NoteHeaderView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.events.SelectionListener

class NoteHeaderViewModel (val notebookId: Long, val selectedNoteHeader: NoteHeader?, val openedFromTabId: String?, parent: CTabFolder) : FormViewModel<NoteHeader>(
    NoteHeaderView(parent, Comparator()),
    NoteHeaderMapper, { NoteHeader.make(notebookId) })
{

    val noteDetails = WritableList<NoteDetail>()
    val noteDetailComparator = NoteDetailViewModel.Comparator()

    init {
        loadData(mapOf("notebookId" to notebookId))
        if (view.form.childFormsContainer != null)
        {
            view.form.childFormsContainer!!.childTabs.forEach { childFormTab: ChildFormTab ->
                wireChildTab(childFormTab, ApplicationData.TAB_KEY_NOTEDETAIL, noteDetailComparator, noteDetails, ::makeNoteDetail)
            }
        }
        onLoad(selectedNoteHeader)
    }

    private fun makeNoteDetail(currentChild: NoteDetail?) : IFormViewModel<NoteDetail> {
        return NoteDetailViewModel(currentEntity!!.id,
            currentChild,
            ApplicationData.TAB_KEY_NOTEHEADER,
            ApplicationData.mainWindow.folder)
    }

    override fun save() {
        super.save()
        afterSave(openedFromTabId)
    }

    override fun changeSelection(){
        val formBindings = super.changeSelection()
        /* specific to child list */
        noteDetails.clear()
        noteDetails.addAll(NoteDetailMapper.getAll(mapOf("noteHeaderId" to currentEntity!!.id)))
    }

    override fun refresh() {
        super.refresh()
        noteDetails.clear()
        noteDetails.addAll(NoteDetailMapper.getAll(mapOf("noteHeaderId" to currentEntity!!.id)))
    }


    override fun getData(parameters: Map<String, Any>): List<NoteHeader> {
        return mapper.getAll(parameters as Map<String, Long>)
    }


    class Comparator : BeansViewerComparator(), IViewerComparator {
        val name_index = 0

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as NoteHeader
            val entity2 = e2 as NoteHeader
            val rc = when (propertyIndex) {
                name_index -> entity1.name.toLowerCase().compareTo(entity2.name.toLowerCase())
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }
}
package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.NoteHeader
import com.parinherm.entity.Notebook
import com.parinherm.entity.schema.NoteHeaderMapper
import com.parinherm.entity.schema.NotebookMapper
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.view.NotebookView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabFolder

class NotebookViewModel  (parent: CTabFolder)  : FormViewModel<Notebook>(
    NotebookView(parent, Comparator()),
    NotebookMapper, { Notebook.make() })  {

    private val noteHeaders = WritableList<NoteHeader>()
    private val noteHeaderComparator = NoteHeaderViewModel.Comparator()


    init {
        if (view.form.childFormsContainer != null)
        {
            view.form.childFormsContainer!!.childTabs.forEach { childFormTab: ChildFormTab ->
                wireChildTab(childFormTab, ApplicationData.TAB_KEY_NOTEHEADER, noteHeaderComparator, noteHeaders, ::makeNoteHeader )
            }
        }
        loadData(mapOf())
    }

    private fun makeNoteHeader(currentChild: NoteHeader?) : IFormViewModel<NoteHeader> {
        return NoteHeaderViewModel(currentEntity!!.id,
            currentChild,
            ApplicationData.TAB_KEY_NOTEBOOK,
            ApplicationData.mainWindow.folder)
    }

    override fun changeSelection(){
        val formBindings = super.changeSelection()
        /* specific to child list */
        noteHeaders.clear()
        noteHeaders.addAll(NoteHeaderMapper.getAll(mapOf("notebookId" to currentEntity!!.id)))
    }

    override fun refresh() {
        super.refresh()
        noteHeaders.clear()
        noteHeaders.addAll(NoteHeaderMapper.getAll(mapOf("notebookId" to currentEntity!!.id)))
    }


    class Comparator : BeansViewerComparator(), IViewerComparator {

        val name_index = 0

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Notebook
            val entity2 = e2 as Notebook
            val rc = when (propertyIndex) {
                name_index -> compareString(entity1.name, entity2.name)
               else -> 0
            }
            return flipSortDirection(rc)
        }
    }

}
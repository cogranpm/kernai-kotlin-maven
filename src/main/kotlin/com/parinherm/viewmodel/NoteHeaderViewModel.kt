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
import com.parinherm.view.NoteDetailView
import com.parinherm.view.NoteHeaderView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.events.SelectionListener

class NoteHeaderViewModel (val notebookId: Long, val selectedNoteHeader: NoteHeader?, val openedFromTabId: String?, parent: CTabFolder) : FormViewModel<NoteHeader>(
    NoteHeaderView(parent, Comparator()),
    NoteHeaderMapper, { NoteHeader.make(notebookId) })
{


    val noteDetails = WritableList<NoteDetail>()
    val noteDetailComparator = NoteDetailViewModel.Comparator()
    val noteDetailContentProvider = ObservableListContentProvider<NoteDetail>()


    init {
        loadData(mapOf("notebookId" to notebookId))
        if (view.form.childFormsContainer != null)
        {
            view.form.childFormsContainer!!.childTabs.forEach { childFormTab: ChildFormTab ->
                wireChildEntity(childFormTab)
            }
        }
        if (selectedNoteHeader != null) {
            val itemInWritableList = dataList.find { it.id == selectedNoteHeader.id }
            if (itemInWritableList != null) {
                view.form.listView.setSelection(StructuredSelection(itemInWritableList), true)
                onListSelection()
            }
        } else {
            new()
        }

    }

    private fun wireChildEntity(childFormTab: ChildFormTab) : Unit {
        val fields = childFormTab.childDefinition[ApplicationData.ViewDef.fields] as List<Map<String, Any>>
        val title = childFormTab.childDefinition[ApplicationData.ViewDef.title] as String

        childFormTab.listView.contentProvider = noteDetailContentProvider
        childFormTab.listView.labelProvider = makeViewerLabelProvider<NoteDetail>(fields, noteDetailContentProvider.knownElements)
        childFormTab.listView.comparator = noteDetailComparator
        childFormTab.listView.input = noteDetails


        childFormTab.listView.addOpenListener {
            // open up a tab to edit child entity
            val selection = childFormTab.listView.structuredSelection
            val selectedItem = selection.firstElement
            // store the selected item in the list in the viewstate
            val currentNoteDetail = selectedItem as NoteDetail
            openNoteDetailTab(currentNoteDetail, title)
        }

        childFormTab.btnAdd.addSelectionListener(SelectionListener.widgetSelectedAdapter { _ ->
            openNoteDetailTab(null, title)
        })

        listHeaderSelection(childFormTab.listView, childFormTab.columns, noteDetailComparator)
    }

    fun openNoteDetailTab(noteDetail: NoteDetail?, title: String){
        val viewModel: IFormViewModel<NoteDetail> = NoteDetailViewModel(currentEntity!!.id,
            noteDetail,
            ApplicationData.TAB_KEY_NOTEDETAIL,
            ApplicationData.mainWindow.folder)
        ApplicationData.makeTab(viewModel, title, ApplicationData.TAB_KEY_NOTEDETAIL)
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
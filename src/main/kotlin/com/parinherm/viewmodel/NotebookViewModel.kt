package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.Ingredient
import com.parinherm.entity.NoteHeader
import com.parinherm.entity.Notebook
import com.parinherm.entity.schema.IngredientMapper
import com.parinherm.entity.schema.NoteHeaderMapper
import com.parinherm.entity.schema.NotebookMapper
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.form.makeViewerLabelProvider
import com.parinherm.view.NotebookView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.events.SelectionListener

class NotebookViewModel  (parent: CTabFolder)  : FormViewModel<Notebook>(
    NotebookView(parent, NotebookViewModel.Comparator()),
    NotebookMapper, { Notebook.make() })  {

    val noteHeaders = WritableList<NoteHeader>()
    val noteHeaderComparator = NoteHeaderViewModel.Comparator()
    val noteHeaderContentProvider = ObservableListContentProvider<NoteHeader>()


    init {
        if (view.form.childFormsContainer != null)
        {
            view.form.childFormsContainer!!.childTabs.forEach { childFormTab: ChildFormTab ->
                wireChildEntity(childFormTab)
            }
        }
        loadData(mapOf())
    }


    private fun wireChildEntity(childFormTab: ChildFormTab) : Unit {
        val fields = childFormTab.childDefinition[ApplicationData.ViewDef.fields] as List<Map<String, Any>>
        val title = childFormTab.childDefinition[ApplicationData.ViewDef.title] as String

        childFormTab.listView.contentProvider = noteHeaderContentProvider
        childFormTab.listView.labelProvider = makeViewerLabelProvider<NoteHeader>(fields, noteHeaderContentProvider.knownElements)
        childFormTab.listView.comparator = noteHeaderComparator
        childFormTab.listView.input = noteHeaders


        childFormTab.listView.addOpenListener {
            // open up a tab to edit child entity
            val selection = childFormTab.listView.structuredSelection
            val selectedItem = selection.firstElement
            // store the selected item in the list in the viewstate
            val currentNoteHeader = selectedItem as NoteHeader
            openNoteHeaderTab(currentNoteHeader, title)
        }

        childFormTab.btnAdd.addSelectionListener(SelectionListener.widgetSelectedAdapter { _ ->
            openNoteHeaderTab(null, title)
        })

        listHeaderSelection(childFormTab.listView, childFormTab.columns, noteHeaderComparator)
    }

    fun openNoteHeaderTab(noteHeader: NoteHeader?, title: String){
        val viewModel: IFormViewModel<NoteHeader> = NoteHeaderViewModel(currentEntity!!.id,
            noteHeader,
            ApplicationData.TAB_KEY_NOTEHEADER,
            ApplicationData.mainWindow.folder)
        ApplicationData.makeTab(viewModel, title, ApplicationData.TAB_KEY_NOTEHEADER)
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
                name_index -> entity1.name.toLowerCase().compareTo(entity2.name.toLowerCase())
               else -> 0
            }
            return flipSortDirection(rc)
        }
    }

}
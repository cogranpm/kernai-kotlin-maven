package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.*
import com.parinherm.entity.schema.*
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.view.NoteSegmentTypeHeaderView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder

class NoteSegmentTypeHeaderViewModel( parent: CTabFolder) : FormViewModel<NoteSegmentTypeHeader>(
    NoteSegmentTypeHeaderView(parent, Comparator()),
    NoteSegmentTypeHeaderMapper,
    { NoteSegmentTypeHeader.make() }) {

    
    private val noteSegmentTypes = WritableList<NoteSegmentType>()
    private val noteSegmentTypeComparator = NoteSegmentTypeViewModel.Comparator()
    

    init {

        if (view.form.childFormsContainer != null)
        {
            view.form.childFormsContainer!!.childTabs.forEach {
                childFormTab: ChildFormTab ->
        
                    wireChildTab(childFormTab, ApplicationData.TAB_KEY_NOTESEGMENTTYPE, noteSegmentTypeComparator, noteSegmentTypes, ::makeNoteSegmentTypesViewModel)
        
            }
        }

        loadData(mapOf())
        
    }






    private fun makeNoteSegmentTypesViewModel(currentChild: NoteSegmentType?) : IFormViewModel<NoteSegmentType> {
        return NoteSegmentTypeViewModel(
        currentEntity!!.id,
        currentChild,
        ApplicationData.TAB_KEY_NOTESEGMENTTYPEHEADER,
        ApplicationData.mainWindow.folder)
        }

    private fun clearAndAddNoteSegmentType() {
        noteSegmentTypes.clear()
        noteSegmentTypes.addAll(NoteSegmentTypeMapper.getAll(mapOf("noteSegmentTypeHeaderId" to currentEntity!!.id)))
    }


    override fun changeSelection(){
        val formBindings = super.changeSelection()
        /* specific to child list */

        clearAndAddNoteSegmentType()

    }

    override fun refresh() {
        super.refresh()

        clearAndAddNoteSegmentType()

    }




    class Comparator : BeansViewerComparator(), IViewerComparator {


        val title_index = 0

        val helpText_index = 1

        val createdDate_index = 2


        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as NoteSegmentTypeHeader
            val entity2 = e2 as NoteSegmentTypeHeader
            val rc = when (propertyIndex) {

                title_index -> compareString(entity1.title, entity2.title)

                helpText_index -> compareString(entity1.helpText, entity2.helpText)

                createdDate_index -> entity1.createdDate.compareTo(entity2.createdDate)

                else -> 0
            }
            return flipSortDirection(rc)
        }
    }


}
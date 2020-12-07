package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.*
import com.parinherm.entity.schema.*
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.view.TopicView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder

class TopicViewModel( val publicationId: Long,  val selectedTopic: Topic?, val openedFromTabId: String?,  parent: CTabFolder) : FormViewModel<Topic>(
    TopicView(parent, Comparator()),
    TopicMapper,
    { Topic.make() }) {

    
    private val notes = WritableList<Note>()
    private val noteComparator = NoteViewModel.Comparator()
    

    init {

        if (view.form.childFormsContainer != null)
        {
            view.form.childFormsContainer!!.childTabs.forEach {
                childFormTab: ChildFormTab ->
        
                    wireChildTab(childFormTab, ApplicationData.TAB_KEY_NOTE, noteComparator, notes, ::makeNotesViewModel)
        
            }
        }

        loadData(mapOf("publicationId" to publicationId))
        onLoad(selectedTopic)
    }



    override fun getData(parameters: Map<String, Any>): List<Topic> {
        return mapper.getAll(parameters as Map<String, Long>)
    }

    override fun save() {
        super.save()
        afterSave(openedFromTabId)
    }





    private fun makeNotesViewModel(currentChild: Note?) : IFormViewModel<Note> {
        return NoteViewModel(
        currentEntity!!.id,
        currentChild,
        ApplicationData.TAB_KEY_TOPIC,
        ApplicationData.mainWindow.folder)
        }

    private fun clearAndAddNote() {
        notes.clear()
        notes.addAll(NoteMapper.getAll(mapOf("topicId" to currentEntity!!.id)))
    }


    override fun changeSelection(){
        val formBindings = super.changeSelection()
        /* specific to child list */

        clearAndAddNote()

    }

    override fun refresh() {
        super.refresh()

        clearAndAddNote()

    }




    class Comparator : BeansViewerComparator(), IViewerComparator {


        val name_index = 0

        val comments_index = 1

        val createdDate_index = 2


        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Topic
            val entity2 = e2 as Topic
            val rc = when (propertyIndex) {

                name_index -> compareString(entity1.name, entity2.name)

                comments_index -> compareString(entity1.comments, entity2.comments)

                createdDate_index -> entity1.createdDate.compareTo(entity2.createdDate)

                else -> 0
            }
            return flipSortDirection(rc)
        }
    }


}
package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.*
import com.parinherm.entity.schema.*
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.view.PublicationView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder

class PublicationViewModel( val subjectId: Long,  val selectedPublication: Publication?, val openedFromTabId: String?,  parent: CTabFolder) : FormViewModel<Publication>(
    PublicationView(parent, Comparator()),
    PublicationMapper,
    { Publication.make() }) {

    
    private val topics = WritableList<Topic>()
    private val topicComparator = TopicViewModel.Comparator()
    

    init {

        if (view.form.childFormsContainer != null)
        {
            view.form.childFormsContainer!!.childTabs.forEach {
                childFormTab: ChildFormTab ->
        
                    wireChildTab(childFormTab, ApplicationData.TAB_KEY_TOPIC, topicComparator, topics, ::makeTopicsViewModel)
        
            }
        }

        loadData(mapOf("subjectId" to subjectId))
        onLoad(selectedPublication)
    }



    override fun getData(parameters: Map<String, Any>): List<Publication> {
        return mapper.getAll(parameters as Map<String, Long>)
    }

    override fun save() {
        super.save()
        afterSave(openedFromTabId)
    }





    private fun makeTopicsViewModel(currentChild: Topic?) : IFormViewModel<Topic> {
        return TopicViewModel(
        currentEntity!!.id,
        currentChild,
        ApplicationData.TAB_KEY_PUBLICATION,
        ApplicationData.mainWindow.folder)
        }

    private fun clearAndAddTopic() {
        topics.clear()
        topics.addAll(TopicMapper.getAll(mapOf("publicationId" to currentEntity!!.id)))
    }


    override fun changeSelection(){
        val formBindings = super.changeSelection()
        /* specific to child list */

        clearAndAddTopic()

    }

    override fun refresh() {
        super.refresh()

        clearAndAddTopic()

    }




    class Comparator : BeansViewerComparator(), IViewerComparator {


        val title_index = 0

        val type_index = 1

        val comments_index = 2

        val createdDate_index = 3


        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Publication
            val entity2 = e2 as Publication
            val rc = when (propertyIndex) {

                title_index -> compareString(entity1.title, entity2.title)

                type_index -> compareLookups(entity1.type, entity2.type, ApplicationData.publicationTypeList)

                comments_index -> compareString(entity1.comments, entity2.comments)

                createdDate_index -> entity1.createdDate.compareTo(entity2.createdDate)

                else -> 0
            }
            return flipSortDirection(rc)
        }
    }


}
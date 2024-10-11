package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.*
import com.parinherm.entity.schema.*
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.lookups.LookupUtils
import com.parinherm.menus.TabInfo
import com.parinherm.view.PublicationView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder

class PublicationViewModel(
    val subjectId: Long,
    val selectedPublication: Publication?,
    val openedFromTabId: String?,
    tabInfo: TabInfo
) : FormViewModel<Publication>(
    PublicationView(tabInfo.folder, Comparator()),
    PublicationMapper,
    { Publication.make(subjectId) },
    tabInfo
) {

    private val topics = WritableList<Topic>()
    private val topicComparator = TopicViewModel.Comparator()

    private val quizs = WritableList<Quiz>()
    private val quizComparator = QuizViewModel.Comparator()


    init {

        if (view.form.childFormsContainer != null) {
            view.form.childFormsContainer!!.childTabs.forEach { childFormTab: ChildFormTab ->
                when (childFormTab.key) {
                    "topic" -> wireChildTab(
                        childFormTab,
                        topicComparator,
                        topics,
                        ::makeTopicsViewModel,
                        TopicMapper
                    )
                    "quiz" -> wireChildTab(
                        childFormTab,
                        quizComparator,
                        quizs,
                        ::makeQuizsViewModel,
                        QuizMapper
                    )
                }
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

    private fun makeTopicsViewModel(currentChild: Topic?): IFormViewModel<Topic> {
        return TopicViewModel(
            currentEntity!!.id,
            currentChild,
            tabId,
            tabInfo.copy(caption = "Topics")
        )
    }

    private fun clearAndAddTopic() {
        topics.clear()
        topics.addAll(TopicMapper.getAll(mapOf("publicationId" to currentEntity!!.id)))
    }

    private fun makeQuizsViewModel(currentChild: Quiz?): IFormViewModel<Quiz> {
        return QuizViewModel(
            currentEntity!!.id,
            currentChild,
            tabId,
            tabInfo.copy(caption = "Quiz")
        )
    }

    private fun clearAndAddQuiz() {
        quizs.clear()
        quizs.addAll(QuizMapper.getAll(mapOf("publicationId" to currentEntity!!.id)))
    }


    override fun changeSelection() {
        val formBindings = super.changeSelection()
        /* specific to child list */
        clearAndAddTopic()
        clearAndAddQuiz()
    }

    override fun refresh() {
        super.refresh()
        clearAndAddTopic()
        clearAndAddQuiz()
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
                type_index -> compareLookups(
                    entity1.type,
                    entity2.type,
                    LookupUtils.getLookupByKey(LookupUtils.publicationTypeLookupKey, false)
                )
                comments_index -> compareString(entity1.comments, entity2.comments)
                createdDate_index -> entity1.createdDate.compareTo(entity2.createdDate)
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }


}
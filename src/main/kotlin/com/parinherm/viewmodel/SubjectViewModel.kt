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
import com.parinherm.view.SubjectView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder

class SubjectViewModel(
    val shelfId: Long,
    val selectedSubject: Subject?,
    val openedFromTabId: String?,
   tabInfo: TabInfo
) : FormViewModel<Subject>(
    SubjectView(tabInfo.folder, Comparator()),
    SubjectMapper,
    { Subject.make(shelfId) },
    tabInfo
) {

    private val publications = WritableList<Publication>()
    private val publicationComparator = PublicationViewModel.Comparator()

    init {
        if (view.form.childFormsContainer != null) {
            view.form.childFormsContainer!!.childTabs.forEach { childFormTab: ChildFormTab ->
                wireChildTab(
                    childFormTab,
                    publicationComparator,
                    publications,
                    ::makePublicationsViewModel,
                    PublicationMapper
                )

            }
        }

        loadData(mapOf("shelfId" to shelfId))
        onLoad(selectedSubject)
    }


    override fun getData(parameters: Map<String, Any>): List<Subject> {
        return mapper.getAll(parameters as Map<String, Long>)
    }

    override fun save() {
        super.save()
        afterSave(openedFromTabId)
    }


    private fun makePublicationsViewModel(currentChild: Publication?): IFormViewModel<Publication> {
        return PublicationViewModel(
            currentEntity!!.id,
            currentChild,
            tabId,
           tabInfo.copy(caption = "Publications")
        )
    }

    private fun clearAndAddPublication() {
        publications.clear()
        publications.addAll(PublicationMapper.getAll(mapOf("subjectId" to currentEntity!!.id)))
    }


    override fun changeSelection() {
        val formBindings = super.changeSelection()
        /* specific to child list */
        clearAndAddPublication()
    }

    override fun refresh() {
        super.refresh()
        clearAndAddPublication()
    }


    class Comparator : BeansViewerComparator(), IViewerComparator {


        val title_index = 0

        val comments_index = 1

        val createdDate_index = 2


        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Subject
            val entity2 = e2 as Subject
            val rc = when (propertyIndex) {

                title_index -> compareString(entity1.title, entity2.title)

                comments_index -> compareString(entity1.comments, entity2.comments)

                createdDate_index -> entity1.createdDate.compareTo(entity2.createdDate)

                else -> 0
            }
            return flipSortDirection(rc)
        }
    }


}
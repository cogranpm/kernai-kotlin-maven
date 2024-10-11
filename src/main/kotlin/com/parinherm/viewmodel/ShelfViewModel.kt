package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.audio.AudioClient
import com.parinherm.audio.AudioDataResult
import com.parinherm.audio.SpeechRecognition
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.*
import com.parinherm.entity.schema.*
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.menus.TabInfo
import com.parinherm.view.ShelfView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.events.FocusAdapter
import org.eclipse.swt.events.FocusEvent
import org.eclipse.swt.events.FocusListener


class ShelfViewModel(tabInfo: TabInfo) : FormViewModel<Shelf>(
    ShelfView(tabInfo.folder, Comparator()),
    ShelfMapper,
    { Shelf.make() },
    tabInfo
) {

    private val subjects = WritableList<Subject>()
    private val subjectComparator = SubjectViewModel.Comparator()

    init {
        if (view.form.childFormsContainer != null) {
            view.form.childFormsContainer!!.childTabs.forEach { childFormTab: ChildFormTab ->

                wireChildTab(
                    childFormTab,
                    subjectComparator,
                    subjects,
                    ::makeSubjectsViewModel,
                    SubjectMapper
                )
            }
        }
        createTab()
        loadData(mapOf())
        view.form.root.addDisposeListener {
            AudioClient.processingFunction = null
        }


        try {
            AudioClient.processingFunction = { command: String, audio: ByteArray, length: Int ->
                if(command.isNotEmpty()){
                    if(command.lowercase() == "add shelf"){
                        new()
                    }
                }
            }
            AudioClient.open()
            SpeechRecognition.open()
        } catch (e: Exception) {
            ApplicationData.logError(e, "Error opening Audio Client")
        }
    }


    private fun makeSubjectsViewModel(currentChild: Subject?): IFormViewModel<Subject> {
        return SubjectViewModel(
            currentEntity!!.id,
            currentChild,
            tabId,
            tabInfo.copy(caption = "Subjects")
        )
    }

    private fun clearAndAddSubject() {
        subjects.clear()
        subjects.addAll(SubjectMapper.getAll(mapOf("shelfId" to currentEntity!!.id)))
    }


    override fun changeSelection() {
        val formBindings = super.changeSelection()
        /* specific to child list */
        clearAndAddSubject()
    }

    override fun refresh() {
        super.refresh()
        clearAndAddSubject()
    }


    class Comparator : BeansViewerComparator(), IViewerComparator {
        val title_index = 0
        val comments_index = 1
        val createdDate_index = 2

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Shelf
            val entity2 = e2 as Shelf
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
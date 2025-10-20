package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.audio.SpeechRecognition
import com.parinherm.audio.speech.SpeechPipelineService
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.*
import com.parinherm.entity.schema.*
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.menus.TabInfo
import com.parinherm.view.AnswerView
import com.parinherm.view.NoteView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder

class NoteViewModel(
    val topicId: Long,
    val selectedNote: Note?,
    val openedFromTabId: String?,
    tabInfo: TabInfo
) : FormViewModel<Note>(
    NoteView(tabInfo.folder, Comparator()),
    NoteMapper,
    { Note.make(topicId) }, tabInfo
) {

    private val noteSegments = WritableList<NoteSegment>()
    private val noteSegmentComparator = NoteSegmentViewModel.Comparator()
    private val noteView = view as NoteView

    init {

        if (view.form.childFormsContainer != null) {
            view.form.childFormsContainer!!.childTabs.forEach { childFormTab: ChildFormTab ->
                wireChildTab(
                    childFormTab,
                    noteSegmentComparator,
                    noteSegments,
                    ::makeNoteSegmentsViewModel,
                    NoteSegmentMapper
                )
            }
        }

        loadData(mapOf("topicId" to topicId))
        onLoad(selectedNote)

        // try the new voice pipleliness class
        val service = SpeechPipelineService(
            recognizer = SpeechRecognition.recognizer!!,
            onFinalResult = { text, audio, length ->
                //audioHandler(text, audio, length)
                println(text)
                currentEntity?.description = currentEntity?.description + text
            },
            onPartialResult = { partial ->
                println(partial)
                //quizRunQuestionView.commandOutput.text = partial
            }
        )

        CoroutineScope(Dispatchers.IO).launch {
            service.start().collect({
               // println("Processed audio chunk")
            })
        }
    }


    override fun getData(parameters: Map<String, Any>): List<Note> {
        return mapper.getAll(parameters as Map<String, Long>)
    }

    override fun save() {
        super.save()
        afterSave(openedFromTabId)
    }

    private fun makeNoteSegmentsViewModel(currentChild: NoteSegment?): IFormViewModel<NoteSegment> {
        return NoteSegmentViewModel(
            currentEntity!!.id,
            currentChild,
            tabId,
            tabInfo.copy(caption = "Note Segments")
        )
    }

    private fun clearAndAddNoteSegment() {
        noteSegments.clear()
        noteSegments.addAll(NoteSegmentMapper.getAll(mapOf("noteId" to currentEntity!!.id)))
    }


    override fun changeSelection() {
        val formBindings = super.changeSelection()
        /* specific to child list */
        clearAndAddNoteSegment()
    }

    override fun refresh() {
        super.refresh()
        clearAndAddNoteSegment()
    }


    class Comparator : BeansViewerComparator(), IViewerComparator {
        val title_index = 0
        val description_index = 1
        val titleAudioFile_index = 2
        val descriptionAudioFile_index = 3
        val createdDate_index = 4

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Note
            val entity2 = e2 as Note
            val rc = when (propertyIndex) {
                title_index -> compareString(entity1.title, entity2.title)
                description_index -> compareString(entity1.description, entity2.description)
                titleAudioFile_index -> compareString(entity1.titleAudioFile, entity2.titleAudioFile)
                descriptionAudioFile_index -> compareString(entity1.descriptionAudioFile, entity2.descriptionAudioFile)
                createdDate_index -> entity1.createdDate.compareTo(entity2.createdDate)
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }


}
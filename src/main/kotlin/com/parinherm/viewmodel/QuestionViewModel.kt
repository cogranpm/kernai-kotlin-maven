package com.parinherm.viewmodel

import com.parinherm.audio.*
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.*
import com.parinherm.entity.schema.*
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.form.makeHeaderText
import com.parinherm.menus.TabInfo
import com.parinherm.view.QuestionView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.SWT
import org.eclipse.swt.events.*


class QuestionViewModel(
    val quizId: Long,
    val selectedQuestion: Question?,
    val openedFromTabId: String?,
    tabInfo: TabInfo
) : FormViewModel<Question>(
    QuestionView(tabInfo.folder, Comparator()),
    QuestionMapper,
    { Question.make(quizId) },
    tabInfo
) {

    private val answers = WritableList<Answer>()
    private val answerComparator = AnswerViewModel.Comparator()
    private val audioList = mutableListOf<AudioDataResult>()
    private var recording = false
    val questionView = view as QuestionView
    private var autoRecordAnswer = false

    init {

        if (view.form.childFormsContainer != null) {
            view.form.childFormsContainer!!.childTabs.forEach { childFormTab: ChildFormTab ->
                when (childFormTab.key) {
                    "answer" -> wireChildTab(
                        childFormTab,
                        answerComparator,
                        answers,
                        ::makeAnswersViewModel,
                        AnswerMapper
                    )
                }
            }
        }

        loadData(mapOf("quizId" to quizId))
        onLoad(selectedQuestion)

        /*
        view.form.childFormsContainer!!.childTabs.forEach { childFormTab: ChildFormTab ->
            when (childFormTab.key) {
                "answer" -> println("Child Tab isShowing: ${childFormTab.tab.isShowing}")
            }
        }
         */
        AudioClient.processingFunction = this::audioHandler
    }

    /*
    private fun shortcuts() {
        questionView.form.parent.children.forEach {
            with(it) {
                addKeyListener(object : KeyAdapter() {
                    override fun keyPressed(e: KeyEvent?) {
                        if ((e!!.stateMask and org.eclipse.swt.SWT.CTRL !== 0) && (e!!.stateMask and org.eclipse.swt.SWT.SHIFT !== 0)) {
                            val keyCode = e!!.keyCode.toChar()
                            when (keyCode) {
                                'p' -> play()
                                's' -> stop()
                                'r' -> record()
                            }
                        }
                    }
                })
            }
        }
    }
     */

    override fun record() {
        SoundPlay.play("ding")
        audioList.clear()
        recording = true
        questionView.commandOutput.text = "recording"
    }

    override fun stop() {
        SoundPlay.play("Windows Default")
        recording = false
        if (currentEntity != null) {
            val stitchedAudio = AudioStitcher.stitch(audioList)
            val (audioData, length) = stitchedAudio
            currentEntity!!.audioBlob = audioData
            currentEntity!!.audioLength = length
        }
        questionView.commandOutput.text = "stopped"
    }

    override fun play() {
        recording = false
        if (currentEntity != null && currentEntity!!.audioBlob != null  && currentEntity!!.audioBlob.size > 0) {
            AudioPlayer.play(currentEntity!!.audioBlob, currentEntity!!.audioLength)
        }
    }

    override fun getData(parameters: Map<String, Any>): List<Question> {
        return mapper.getAll(parameters as Map<String, Long>)
    }

    override fun save() {
        if(recording){
            stop()
        } else {
            SoundPlay.play("Windows Default")
        }
        super.save()
        afterSave(openedFromTabId)
        questionView.commandOutput.text = "saved"
        if (answers.size == 0 ) {
            //currentEntity!!.audioBlob != null && currentEntity!!.audioBlob.size > 0
            openTab((::makeAnswersViewModel)(null, this.autoRecordAnswer))
        }
    }

    private fun makeAnswersViewModel(currentChild: Answer?, autoRecord: Boolean = false): IFormViewModel<Answer> {
        return AnswerViewModel(
            currentEntity!!.id,
            currentChild,
            tabId,
            tabInfo.copy(caption = "Answers"),
            autoRecord
        )
    }

    private fun clearAndAddAnswer() {
        answers.clear()
        answers.addAll(AnswerMapper.getAll(mapOf("questionId" to currentEntity!!.id)))
    }


    override fun changeSelection() {
        val formBindings = super.changeSelection()
        if (currentEntity != null && currentEntity!!.audioBlob.isEmpty()) {
            currentEntity!!.audioBlob = QuestionMapper.loadAudio(currentEntity!!.id)
        }
        clearAndAddAnswer()
    }

    override fun refresh() {
        super.refresh()
        clearAndAddAnswer()
    }

    override fun activated() {
        AudioClient.processingFunction = this::audioHandler
    }


    private fun audioHandler(command: String, audio: ByteArray, length: Int) {
        if (recording) {
            val cleanCommand = command.trimStart().trimEnd().lowercase()
            if (cleanCommand == "stop" || cleanCommand == "save" || cleanCommand == "close" || cleanCommand == "answer") {
                for (i in 1..200) audioList.remove(audioList.last())
                stop()
                if(cleanCommand == "answer"){
                    this.autoRecordAnswer = true
                } else {
                    this.autoRecordAnswer = false
                }
                save()
            } else {
                audioList.add(AudioDataResult(audio to length))
                if (currentEntity != null && command.trimStart().trimEnd().isNotEmpty()) {
                    currentEntity?.let {
                        if (it.body.isNotEmpty()) {
                            it.body = it.body + " "
                        }
                        it.body = it.body + command
                    }
                }
            }
        } else if (command.isNotEmpty()) {
            val cleanCommand = command.trimStart().trimEnd().lowercase()
            questionView.commandOutput.text = cleanCommand
            when (cleanCommand) {
                "add" , "new", "put", "create" -> {
                    new()
                    record()
                }

                "save" -> {
                    save()
                    //openTab((::makeAnswersViewModel)(null, true))
                }

                "add answer", "answer", "new answer" -> openTab((::makeAnswersViewModel)(null, true))
                "record" -> record()
                "play" -> play()
            }
        }
    }


    class Comparator : BeansViewerComparator(), IViewerComparator {
        val body_index = 0
        val createdDate_index = 2

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Question
            val entity2 = e2 as Question
            val rc = when (propertyIndex) {
                body_index -> compareString(entity1.body, entity2.body)
                createdDate_index -> entity1.createdDate.compareTo(entity2.createdDate)
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }
}
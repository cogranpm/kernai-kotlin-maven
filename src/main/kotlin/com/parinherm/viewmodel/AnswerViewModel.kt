package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.audio.*
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.*
import com.parinherm.entity.schema.*
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.menus.TabInfo
import com.parinherm.view.AnswerView
import com.parinherm.view.QuestionView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.events.KeyAdapter
import org.eclipse.swt.events.KeyEvent
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent

class AnswerViewModel(
    val questionId: Long,
    val selectedAnswer: Answer?,
    val openedFromTabId: String?,
    tabInfo: TabInfo,
    val autoRecordMode: Boolean = false
) : FormViewModel<Answer>(
    AnswerView(tabInfo.folder, Comparator()),
    AnswerMapper,
    { Answer.make(questionId) },
    tabInfo
) {

    private val audioList = arrayListOf<AudioDataResult>()
    private var recording = false
    private val answerView = view as AnswerView

    init {
        loadData(mapOf("questionId" to questionId))
        onLoad(selectedAnswer)

       shortcuts()
        AudioClient.processingFunction = this::audioHandler
        if(currentEntity != null){
            if(currentEntity!!.id == 0L){
                //record()
            }
        }
        if(autoRecordMode){
            record()
        }
    }

    private fun shortcuts(){
        with (answerView.commandOutput){
            addKeyListener(object: KeyAdapter(){
                override fun keyPressed(e: KeyEvent?) {
                    if((e!!.stateMask and org.eclipse.swt.SWT.CTRL !== 0) && (e!!.stateMask and org.eclipse.swt.SWT.SHIFT !== 0) ){
                        val keyCode = e!!.keyCode.toChar()
                        println(keyCode)
                        when(keyCode){
                           'c' -> close()
                        }
                    }
                }
            })
        }
    }


    override fun getData(parameters: Map<String, Any>): List<Answer> {
        return mapper.getAll(parameters as Map<String, Long>)
    }

    override fun save() {
        if(recording){
            recording = false
            SoundPlay.play("ding")
            if (currentEntity != null) {
                val stitchedAudio = AudioStitcher.stitch(audioList)
                val (audioData, length) = stitchedAudio
                currentEntity!!.audioBlob = audioData
                currentEntity!!.audioLength = length
            }
        } else {
            SoundPlay.play("ding")
        }
        super.save()
        afterSave(openedFromTabId)
        answerView.commandOutput.text = "saved"
    }


    override fun activated() {
        AudioClient.processingFunction = this::audioHandler
    }

    private fun audioHandler(command: String, audio: ByteArray, length: Int) {
        if (recording) {
            val cleanCommand = command.trimStart().trimEnd().lowercase()
            if (cleanCommand == "stop" || cleanCommand == "save") {
                stop()
                save()
            } else if(cleanCommand == "close"){
                stop()
                save()
                close()
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
        } else {
            val cleanCommand = command.trimStart().trimEnd().lowercase()
            answerView.commandOutput.text = cleanCommand
            when (cleanCommand) {
                "add" -> new()
                "save" -> save()
                "record" -> record()
                "play" -> play()
                "close" -> close()
            }
        }
    }

    private fun close() {
        val questionTab = ApplicationData.tabs[openedFromTabId]
        if (questionTab != null) {
            if(questionTab.viewModel != null){
                questionTab.viewModel.activated()
            }
            tabInfo.folder.selection = questionTab.tab
            ApplicationData.tabs[tabId]?.tab?.dispose()
        }
    }

    override fun play() {
        recording = false
        if (currentEntity != null && currentEntity!!.audioBlob != null) {
            AudioPlayer.play(currentEntity!!.audioBlob, currentEntity!!.audioLength)
        }
    }

    override fun record() {
        SoundPlay.play("ding")
        audioList.clear()
        recording = true
        answerView.commandOutput.text = "recording"
    }

    override fun stop() {
        save()
        answerView.commandOutput.text = "stopped"
    }

    class Comparator : BeansViewerComparator(), IViewerComparator {


        val body_index = 0

        val bodyFile_index = 1

        val answerType_index = 2

        val createdDate_index = 3


        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Answer
            val entity2 = e2 as Answer
            val rc = when (propertyIndex) {

                body_index -> compareString(entity1.body, entity2.body)

                bodyFile_index -> compareString(entity1.bodyFile, entity2.bodyFile)

                answerType_index -> entity1.answerType.compareTo(entity2.answerType)

                createdDate_index -> entity1.createdDate.compareTo(entity2.createdDate)

                else -> 0
            }
            return flipSortDirection(rc)
        }
    }


}
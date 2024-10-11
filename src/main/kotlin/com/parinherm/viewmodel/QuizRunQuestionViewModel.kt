package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.audio.*
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.Answer
import com.parinherm.entity.Question
import com.parinherm.entity.QuizRunQuestion
import com.parinherm.entity.schema.AnswerMapper
import com.parinherm.entity.schema.QuestionMapper
import com.parinherm.entity.schema.QuizRunQuestionMapper
import com.parinherm.form.FormViewModel
import com.parinherm.menus.TabInfo
import com.parinherm.view.QuizRunQuestionView
import org.eclipse.jface.dialogs.MessageDialog
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import java.applet.Applet
import kotlin.math.roundToInt


class QuizRunQuestionViewModel(
    val quizRunId: Long,
    val selectedQuizRunQuestion: QuizRunQuestion?,
    val openedFromTabId: String?,
    tabInfo: TabInfo
) : FormViewModel<QuizRunQuestion>(
    QuizRunQuestionView(tabInfo.folder, QuizRunQuestionComparator()),
    QuizRunQuestionMapper,
    { QuizRunQuestion.make(quizRunId) }, tabInfo
) {

    var currentQuestion: Question? = null
    var currentAnswer: Answer? = null
    val quizRunQuestionView = view as QuizRunQuestionView
    private val audioList = arrayListOf<AudioDataResult>()
    private var recording = false
    val table = this.view.form.listView.table
    var isDirty = false
    var initializing = false
    val questionsList = mutableListOf<QuizRunQuestion>()

    init {
        initializing = true
        loadData(mapOf("quizRunId" to quizRunId))
        onLoad(selectedQuizRunQuestion)
        AudioClient.processingFunction = this::audioHandler
        incorrect(false)
        isDirty = false

        quizRunQuestionView.playQuestionButton.addSelectionListener(object: SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
               playQuestion()
            }
        })

        quizRunQuestionView.playAnswerButton.addSelectionListener(object: SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                playAnswer()
            }
        })


        quizRunQuestionView.nextButton.addSelectionListener(object: SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
               next()
            }
        })

        quizRunQuestionView.previousButton.addSelectionListener(object: SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                previous()
            }
        })
        initializing = false
    }

    private fun audioHandler(command: String, audio: ByteArray, length: Int){
        val cleanCommand = command.trimStart().trimEnd().lowercase()
        if (recording) {
            if (cleanCommand == "stop" || cleanCommand == "save" || cleanCommand == "close") {
                stop()
            }
            else {
                audioList.add(AudioDataResult(audio to length))
                if (currentEntity != null && cleanCommand.isNotEmpty()) {
                    currentEntity?.let {
                        if (it.body.isNotEmpty()) {
                            it.body = it.body + " "
                        }
                        it.body = it.body + command
                    }
                }
                if(cleanCommand.endsWith("stop", true))
                {
                    stop()
                }
            }
        } else if (command.isNotEmpty()) {
            val cleanCommand = command.trimStart().trimEnd().lowercase()
            quizRunQuestionView.commandOutput.text = cleanCommand
            when (cleanCommand){
                "save" -> save()
                "record" -> record()
                "play" -> play()
                "next" -> next()
                "previous" -> previous()
                "question", "play question" -> playQuestion()
                "answer", "play answer" -> playAnswer()
                "correct" -> correct()
                "incorrect" -> incorrect()
                "correct next" -> {
                    correct()
                    next()
                }
                "incorrect next" -> {
                    incorrect()
                    next()
                }
            }
        }
    }

    override fun activated() {
        AudioClient.processingFunction = this::audioHandler
    }

    override fun record() {
        SoundPlay.play("ding")
        audioList.clear()
        recording = true
        quizRunQuestionView.commandOutput.text = "recording"
    }

    override fun stop() {
        recording = false
        isDirty = true
        SoundPlay.play("ding")
        quizRunQuestionView.commandOutput.text = "stopped"
        with(currentAnswer) {
            if (this != null) {
                quizRunQuestionView.answerText.text = this.body
            }
        }
        if (currentEntity != null) {
            val stitchedAudio = AudioStitcher.stitch(audioList)
            val (audioData, length) = stitchedAudio
            currentEntity!!.audioblob = audioData
            currentEntity!!.audiolength = length
            //save()
        }
    }

    override fun play() {
        recording = false
        if (currentEntity != null && currentEntity!!.audioblob != null) {
            AudioPlayer.play(currentEntity!!.audioblob, currentEntity!!.audiolength)
        }
    }

    fun playQuestion(){
        with(currentQuestion){
            if(this != null){
                AudioPlayer.play(this.audioBlob, this.audioLength)
            }
        }
    }

    fun playAnswer(){
        with(currentAnswer){
            if(this != null){
                AudioPlayer.play(this.audioBlob, this.audioLength)
                quizRunQuestionView.correctAnswerText.text = this.body
            }
        }
    }

    fun next(){
        val nextIndex = table.selectionIndex + 1
        if(nextIndex <= view.form.listView.table.itemCount){
            save()
        }
        if(nextIndex < view.form.listView.table.itemCount){
            view.form.listView.setSelection(StructuredSelection(view.form.listView.getElementAt(nextIndex)))
        }
        else {
            val totalCount = view.form.listView.table.itemCount + 0.0
            val totalCorrect = this.questionsList.filter{ e -> e.mark > 0}
            val totalCorrectCount = totalCorrect.size.toDouble()
            val testPercent = ((totalCorrectCount / totalCount)  * 100).roundToInt()
            quizRunQuestionView.nextButton.enabled = false
            MessageDialog.openInformation(
                ApplicationData.mainWindow.shell,
                "Quiz is complete",
                "Quiz complete, your score is ${testPercent}%. Total Questions: ${totalCount.roundToInt()} Total Correct: ${totalCorrectCount.roundToInt()}");
        }
    }

    fun previous(){
        quizRunQuestionView.nextButton.enabled = true
        val prevIndex = table.selectionIndex - 1
        if(prevIndex > -1){
            save()
            view.form.listView.setSelection(StructuredSelection(view.form.listView.getElementAt(prevIndex)))
        }
    }

    fun correct(){
        with(currentEntity){
            if(this != null){
                SoundPlay.play("ding")
                this.mark = 1
                isDirty = true
                //save()
            }
        }
    }

    fun incorrect(playSound: Boolean = true) {
        with(currentEntity){
            if(this != null){
                if(playSound){SoundPlay.play("ding")}
                this.mark = -1
                isDirty = true
                //save()
            }
        }
    }


    override fun getData(parameters: Map<String, Any>): List<QuizRunQuestion> {
        this.questionsList.addAll(0, mapper.getAll(parameters as Map<String, Long>))
        return this.questionsList
    }

    override fun save() {
        if (!isDirty || initializing) return
        super.save()
        isDirty = false

        //with a lot of child data the performance of this is terrible
        //afterSave(openedFromTabId)
    }

    override fun changeSelection() {
        super.changeSelection()
        if(currentEntity != null){
            currentEntity!!.mark = -1
            if(currentEntity!!.audioblob.isEmpty()){
                currentEntity!!.audioblob= QuizRunQuestionMapper.loadAudio(currentEntity!!.id)
            }
            currentQuestion = QuestionMapper.load(currentEntity!!.questionId)
            if(currentQuestion != null){
                val answers = AnswerMapper.getAll(mapOf("questionId" to currentQuestion!!.id))
                quizRunQuestionView.questionText.text = currentQuestion!!.body
                quizRunQuestionView.answerText.text = "play answer to reveal"
                quizRunQuestionView.correctAnswerText.text = ""
                if(answers.isNotEmpty()){
                    currentAnswer = answers.first()
                    playQuestion()
                }
            }
        }
    }


    class QuizRunQuestionComparator : BeansViewerComparator(), IViewerComparator {


        val questionId_index = 0

        val audioblob_index = 1

        val audiolength_index = 2

        val mark_index = 3


        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            return 0
            /*
            val entity1 = e1 as QuizRunQuestion
            val entity2 = e2 as QuizRunQuestion
            val rc = when (propertyIndex) {

                questionId_index -> entity1.questionId.compareTo(entity2.questionId)

                audioblob_index -> compareString("a", "b")

                audiolength_index -> entity1.audiolength.compareTo(entity2.audiolength)

                mark_index -> entity1.mark.compareTo(entity2.mark)

                else -> 0
            }
            return flipSortDirection(rc)
             */
        }
    }


}

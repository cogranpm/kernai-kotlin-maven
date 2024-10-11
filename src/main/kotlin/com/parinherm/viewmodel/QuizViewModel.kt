package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.*
import com.parinherm.entity.schema.*
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.form.applyLayoutToField
import com.parinherm.lookups.LookupUtils
import com.parinherm.menus.TabInfo
import com.parinherm.pickers.TopicPicker
import com.parinherm.script.ScriptUtils
import com.parinherm.view.QuizView
import com.parinherm.view.SnippetView
import org.eclipse.core.databinding.Binding
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.ArrayContentProvider
import org.eclipse.jface.viewers.ComboViewer
import org.eclipse.jface.viewers.LabelProvider
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Text
import java.lang.Exception
import java.time.LocalDateTime

class QuizViewModel(
    val publicationId: Long,
    val selectedQuiz: Quiz?,
    val openedFromTabId: String?,
    tabInfo: TabInfo
) : FormViewModel<Quiz>(
    QuizView(tabInfo.folder, Comparator()),
    QuizMapper,
    { Quiz.make(publicationId) },
    tabInfo
) {

    private val questions = WritableList<Question>()
    private val questionComparator = QuestionViewModel.Comparator()
    private val quizRuns = WritableList<QuizRun>()
    private val quizRunComparator = QuizRunViewModel.QuizRunComparator()

    init {
        val quizView = view as QuizView

        TopicPicker.makeDataSource(publicationId)
        val topicViewer = view.form.formWidgets["topicId"]
        if (topicViewer != null) {
            val viewer = topicViewer.widget as ComboViewer
            viewer.input = TopicPicker.dataSource
            viewer.refresh()
        }

        if (view.form.childFormsContainer != null) {
            view.form.childFormsContainer!!.childTabs.forEach { childFormTab: ChildFormTab ->
                when (childFormTab.key) {
                    "question" -> wireChildTab(
                        childFormTab,
                        questionComparator,
                        questions,
                        ::makeQuestionsViewModel,
                        QuestionMapper
                    )

                    "quizrun" -> wireChildTab(
                        childFormTab,
                        quizRunComparator,
                        quizRuns,
                        ::makeQuizRunViewModel,
                        QuizRunMapper
                    )
                }
                // wireChildTab(childFormTab, questionComparator, questions, ::makeQuestionsViewModel, QuestionMapper)
            }
        }

        quizView.genQuizBtn.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                if (currentEntity != null) {
                    Display.getDefault().asyncExec {
                        try {
                            Display.getDefault().activeShell.cursor = Display.getDefault().getSystemCursor(SWT.CURSOR_WAIT)
                            generateQuizRun()
                            clearAndAddQuizRun()
                        } catch (e: Exception) {
                            ApplicationData.logError(e, "Error when generating quiz run")
                       }
                        finally {
                            Display.getDefault().activeShell.cursor = null
                        }
                    }
                }
            }
        })


        loadData(mapOf("publicationId" to publicationId))
        onLoad(selectedQuiz)

    }

    private fun generateQuizRun(){
        val quizRun: QuizRun = QuizRun(0, currentEntity!!.id, LocalDateTime.now())
        QuizRunMapper.save(quizRun)
        val questions = QuestionMapper.getAllKeysByQuizId(currentEntity!!.id)
        questions.forEach{
            val quizRunQuestion = QuizRunQuestion.make(quizRun.id)
            quizRunQuestion.questionId = it
            QuizRunQuestionMapper.save(quizRunQuestion)
        }
    }

    override fun getData(parameters: Map<String, Any>): List<Quiz> {
        return mapper.getAll(parameters as Map<String, Long>)
    }

    override fun save() {
        super.save()
        afterSave(openedFromTabId)
    }

    private fun makeQuestionsViewModel(currentChild: Question?): IFormViewModel<Question> {
        return QuestionViewModel(
            currentEntity!!.id,
            currentChild,
            tabId,
            tabInfo.copy(caption = "Questions")
        )
    }


    private fun makeQuizRunViewModel(currentChild: QuizRun?): IFormViewModel<QuizRun> {
        return QuizRunViewModel(
            currentEntity!!.id,
            currentChild,
            tabId,
            tabInfo.copy(caption = "Quiz Runs")
        )
    }

    private fun clearAndAddQuestion() {
        questions.clear()
        questions.addAll(QuestionMapper.getAll(mapOf("quizId" to currentEntity!!.id)))
    }

    private fun clearAndAddQuizRun() {
        quizRuns.clear()
        quizRuns.addAll(QuizRunMapper.getAll(mapOf("quizId" to currentEntity!!.id)))
    }

    override fun changeSelection() {
        val formBindings = super.changeSelection()
        /* specific to child list */
        clearAndAddQuestion()
        clearAndAddQuizRun()
    }

    override fun refresh() {
        super.refresh()
        clearAndAddQuestion()
        clearAndAddQuizRun()
    }

    class Comparator : BeansViewerComparator(), IViewerComparator {
        val name_index = 0
        val topicId_index = 1
        val createdDate_index = 2

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Quiz
            val entity2 = e2 as Quiz
            val rc = when (propertyIndex) {
                name_index -> compareString(entity1.name, entity2.name)
                topicId_index -> entity1.topicId.compareTo(entity2.topicId)
                createdDate_index -> entity1.createdDate.compareTo(entity2.createdDate)
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }


}
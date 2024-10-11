package com.parinherm.viewmodel

import com.parinherm.menus.TabInfo
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator

import com.parinherm.entity.schema.QuizRunQuestionMapper
import com.parinherm.entity.QuizRunQuestion

import com.parinherm.entity.QuizRun
import com.parinherm.entity.schema.QuestionMapper
import com.parinherm.entity.schema.QuizRunMapper
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.script.ScriptUtils
import com.parinherm.view.QuizRunView
import com.parinherm.view.SnippetView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Text
import java.lang.Exception
import java.time.LocalDateTime


class QuizRunViewModel(
    val quizId: Long,
    val selectedQuizRun: QuizRun?,
    val openedFromTabId: String?,
    tabInfo: TabInfo
) : FormViewModel<QuizRun>(
    QuizRunView(tabInfo.folder, QuizRunComparator()),
    QuizRunMapper,
    { QuizRun.make(quizId) }, tabInfo
) {


    private val quizRunQuestions = WritableList<QuizRunQuestion>()
    private val quizRunQuestionComparator = QuizRunQuestionViewModel.QuizRunQuestionComparator()

    init {

        if (view.form.childFormsContainer != null) {
            view.form.childFormsContainer!!.childTabs.forEach { childFormTab: ChildFormTab ->
                when (childFormTab.key) {

                    "quizrunquestion" -> wireChildTab(
                        childFormTab,
                        quizRunQuestionComparator,
                        quizRunQuestions,
                        ::makeQuizRunQuestionsViewModel,
                        QuizRunQuestionMapper
                    )

                }
            }
        }


        loadData(mapOf("quizId" to quizId))
        onLoad(selectedQuizRun)

        val quizRunView = view as QuizRunView

        quizRunView.runButton.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                if (currentEntity != null) {
                    //open new tab with first question
                    val questionViewModel = makeQuizRunQuestionsViewModel(quizRunQuestions[0])
                    openTab(questionViewModel)
                }
            }
        })


         quizRunView.revisionButton.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
               if ( currentEntity != null) {
                    Display.getDefault().asyncExec {
                        try {
                            Display.getDefault().activeShell.cursor = Display.getDefault().getSystemCursor(SWT.CURSOR_WAIT)

                            //get all the wrongly answered questions for this quiz run
                            val questions = QuizRunQuestionMapper.getRevisionQuestionsByQuizRunId(currentEntity!!.id)

                            //create a new quiz run to house them
                            val newQuizRun = QuizRun(0, currentEntity!!.quizId, LocalDateTime.now())
                            QuizRunMapper.save(newQuizRun)

                            //put the wrongly answered questions in new quiz run
                            questions.forEach{
                                val quizRunQuestion = QuizRunQuestion.make(newQuizRun.id)
                                quizRunQuestion.questionId = it
                                QuizRunQuestionMapper.save(quizRunQuestion)
                            }

                        } catch (e: Exception) {
                            Display.getDefault().timerExec(200) {
                            }
                        } finally {
                            Display.getDefault().activeShell.cursor = null
                            //update the list in the tab
                            afterSave(openedFromTabId)
                        }
                    }
                }
            }
        })

    }


    override fun getData(parameters: Map<String, Any>): List<QuizRun> {
        return mapper.getAll(parameters as Map<String, Long>)
    }

    override fun save() {
        super.save()
        afterSave(openedFromTabId)
    }

    override fun new() {
        super.new()
        if (currentEntity != null) {
            currentEntity!!.CreatedDate = LocalDateTime.now()
            QuizRunMapper.save(currentEntity!!)
            val questions = QuestionMapper.getAllKeysByQuizId(quizId)
            questions.forEach{
                val quizRunQuestion = QuizRunQuestion.make(currentEntity!!.id)
                quizRunQuestion.questionId = it
                QuizRunQuestionMapper.save(quizRunQuestion)
            }
            clearAndAddQuizRunQuestion()
        }
    }

    private fun makeQuizRunQuestionsViewModel(currentChild: QuizRunQuestion?): IFormViewModel<QuizRunQuestion> {
        return QuizRunQuestionViewModel(
            currentEntity!!.id,
            currentChild,
            tabId,
            tabInfo.copy(caption = "Quiz Run Question")
        )
    }

    private fun clearAndAddQuizRunQuestion() {
        quizRunQuestions.clear()
        quizRunQuestions.addAll(QuizRunQuestionMapper.getAll(mapOf("quizRunId" to currentEntity!!.id)))
    }


    override fun changeSelection() {
        val formBindings = super.changeSelection()
        clearAndAddQuizRunQuestion()
    }

    override fun refresh() {
        super.refresh()
        clearAndAddQuizRunQuestion()
    }


    class QuizRunComparator : BeansViewerComparator(), IViewerComparator {

        val CreatedDate_index = 0
        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as QuizRun
            val entity2 = e2 as QuizRun
            val rc = when (propertyIndex) {

                CreatedDate_index -> entity1.CreatedDate.compareTo(entity2.CreatedDate)

                else -> 0
            }
            return flipSortDirection(rc)
        }
    }


}

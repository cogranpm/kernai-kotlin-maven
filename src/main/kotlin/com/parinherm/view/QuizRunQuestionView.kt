package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.QuizRunQuestion
import com.parinherm.form.Form
import com.parinherm.form.makeHeaderText
import com.parinherm.form.makeRecordingToolbar
import com.parinherm.image.ImageUtils
import com.parinherm.server.DefaultViewDefinitions
import com.parinherm.view.filter.RecipeViewFilter
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Text

class QuizRunQuestionView(val parent: Composite, comparator: BeansViewerComparator) : View<QuizRunQuestion> {

    override val form: Form<QuizRunQuestion> = Form(
        parent,
        DefaultViewDefinitions.loadView("quizrunquestion"),
        comparator
    )

    private val editToolbar = form.toolbar
    val playQuestionButton = Button(editToolbar, SWT.PUSH)
    val questionText = Text(editToolbar, SWT.READ_ONLY)
    val playAnswerButton = Button(editToolbar, SWT.PUSH)
    val answerText = Text(editToolbar, SWT.READ_ONLY)
    val correctAnswerText = Text(editToolbar, SWT.MULTI or SWT.BORDER or SWT.V_SCROLL or SWT.WRAP or SWT.READ_ONLY )
    val nextButton = Button(editToolbar, SWT.PUSH)
    val previousButton = Button(editToolbar, SWT.PUSH)
    val commandOutput = Text(form.headerSection, SWT.READ_ONLY)

    init {
        makeHeaderText(form.headerSection, "Record your answer")
        with(playQuestionButton) {
            image = ImageUtils.getImage("media-playback-start")
            text = "Play &Question"
            toolTipText = "play question"
        }
        with(playAnswerButton) {
            image = ImageUtils.getImage("media-playback-start")
            text = "Play &Answer"
            toolTipText = "play answer"
        }
        with(nextButton){
            image = ImageUtils.getImage("go-next")
            text = "Next"
        }
        with(previousButton){
            image = ImageUtils.getImage("go-previous")
            text = "Previous"
        }
        GridDataFactory.fillDefaults().grab(true, false).applyTo(commandOutput)
        GridDataFactory.fillDefaults().grab(true, true).applyTo(questionText)
        GridDataFactory.fillDefaults().grab(false, false).applyTo(playQuestionButton)
        GridDataFactory.fillDefaults().grab(false, false).applyTo(playAnswerButton)
        GridDataFactory.fillDefaults().grab(true, true).applyTo(answerText)
        GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 240).applyTo(correctAnswerText)
        GridDataFactory.fillDefaults().grab(false, false).applyTo(nextButton)
        GridDataFactory.fillDefaults().grab(false, false).applyTo(previousButton)
    }


}

package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.QuizRun
import com.parinherm.form.Form
import com.parinherm.form.makeHeaderText
import com.parinherm.image.ImageUtils
import com.parinherm.server.DefaultViewDefinitions
import com.parinherm.view.filter.RecipeViewFilter
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite

class QuizRunView(val parent: Composite, comparator: BeansViewerComparator)
    : View <QuizRun> {

        override val form: Form<QuizRun> = Form(parent,
            DefaultViewDefinitions.loadView("quizrun"),
            comparator
        )

    val editToolbar = form.toolbar
    val runButton = Button(editToolbar, SWT.PUSH)
    val revisionButton = Button(editToolbar, SWT.PUSH)

    init {
        makeHeaderText(form.headerSection, "Revision will duplicate Quiz Run with incorrectly answered questions only.")

        runButton.image = ImageUtils.getImage("go-last")
        runButton.text = "Start"
        runButton.toolTipText = "Start Quiz"

        revisionButton.image = ImageUtils.getImage("bookmark-new")
        revisionButton.text = "Revise"
        revisionButton.toolTipText = "Revise incorrect answers"

        GridDataFactory.fillDefaults().grab(false, false).applyTo(runButton)
        GridDataFactory.fillDefaults().grab(false, false).applyTo(revisionButton)
    }


}

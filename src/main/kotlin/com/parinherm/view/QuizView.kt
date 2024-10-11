package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Quiz
import com.parinherm.form.Form
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.form.makeHeaderText
import com.parinherm.image.ImageUtils
import com.parinherm.server.DefaultViewDefinitions
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite

class QuizView(val parent: Composite, comparator: BeansViewerComparator)
    : View <Quiz> {

        override val form: Form<Quiz> = Form(parent,
            DefaultViewDefinitions.loadView(ViewDefConstants.quizViewId),
            comparator
        )
    val editToolbar = form.toolbar
    val genQuizBtn = Button(editToolbar, SWT.PUSH)

    init {
        genQuizBtn.image = ImageUtils.getImage("go-top")
        genQuizBtn.text = "Generate quiz run"
        genQuizBtn.toolTipText = "Generate a quiz run for this quiz"
        GridDataFactory.fillDefaults().grab(false, false).applyTo(genQuizBtn)
    }

}
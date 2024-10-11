package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Answer
import com.parinherm.form.Form
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.form.makeHeaderText
import com.parinherm.form.makeRecordingToolbar
import com.parinherm.server.DefaultViewDefinitions
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Text

class AnswerView(val parent: Composite, comparator: BeansViewerComparator)
    : View <Answer> {

        override val form: Form<Answer> = Form(parent,
            DefaultViewDefinitions.loadView(ViewDefConstants.answerViewId),
            comparator
        )

    private val editToolbar = form.toolbar
    //val recordingToolbar = makeRecordingToolbar(editToolbar)
    val commandOutput = Text(form.headerSection, SWT.READ_ONLY)

    init {
        makeHeaderText(form.headerSection, "Record the Answer - Commands: add stop save close play")
        GridDataFactory.fillDefaults().grab(true, false).applyTo(commandOutput)
    }
}
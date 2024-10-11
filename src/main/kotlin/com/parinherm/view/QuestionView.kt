package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Question
import com.parinherm.form.Form
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.form.makeHeaderText
import com.parinherm.form.makeRecordingToolbar
import com.parinherm.image.ImageUtils
import com.parinherm.server.DefaultViewDefinitions
import org.eclipse.jface.action.ToolBarManager
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.swt.SWT
import org.eclipse.swt.events.KeyAdapter
import org.eclipse.swt.events.KeyEvent
import org.eclipse.swt.widgets.*

class QuestionView(val parent: Composite, comparator: BeansViewerComparator) : View<Question> {

    override val form: Form<Question> = Form(
        parent,
        DefaultViewDefinitions.loadView(ViewDefConstants.questionViewId),
        comparator
    )

    private val editToolbar = form.toolbar
    //val recordingToolbar = makeRecordingToolbar(editToolbar)
    val commandOutput = Text(form.headerSection, SWT.READ_ONLY)

    init {
        GridDataFactory.fillDefaults().grab(true, false).applyTo(commandOutput)
        makeHeaderText(form.headerSection, "Record the question. Commands: stop save add [add answer] answer [new answer] record play")

    }

}
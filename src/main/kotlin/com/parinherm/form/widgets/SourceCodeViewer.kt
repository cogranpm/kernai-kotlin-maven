package com.parinherm.form.widgets

import com.parinherm.form.applyLayoutToField
import com.parinherm.view.KotlinSourceViewerConfiguration
import org.eclipse.jface.text.Document
import org.eclipse.jface.text.DocumentEvent
import org.eclipse.jface.text.IDocument
import org.eclipse.jface.text.IDocumentListener
import org.eclipse.jface.text.source.*
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite


val VERTICAL_RULER_WIDTH = 12

class SourceCodeViewer(parent: Composite) : SourceViewer(
    parent,
    CompositeRuler(VERTICAL_RULER_WIDTH),
    OverviewRuler(null, VERTICAL_RULER_WIDTH, null),
    false,
    SWT.BORDER or SWT.MULTI or SWT.V_SCROLL or SWT.H_SCROLL
) {

    //private val daDocument: IDocument = Document()
    val annotationModel = AnnotationModel()


    init {
        document = Document()
        annotationModel.connect(document)
        setDocument(document, annotationModel)

        document.addDocumentListener(object: IDocumentListener {
            override fun documentAboutToBeChanged(p0: DocumentEvent?) {

            }

            override fun documentChanged(p0: DocumentEvent?) {
                //currentEntity?.body = document.get()
            }
        })

        (verticalRuler as CompositeRuler).addDecorator(0, LineNumberRulerColumn())
        configure(KotlinSourceViewerConfiguration())
        applyLayoutToField(control, true, true)
    }
}
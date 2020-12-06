package com.parinherm.form.widgets

import com.parinherm.form.applyLayoutToField
import org.eclipse.jface.text.Document
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
        (verticalRuler as CompositeRuler).addDecorator(0, LineNumberRulerColumn())
        configure(KotlinSourceViewerConfiguration())
        applyLayoutToField(control, true, true)
    }
}
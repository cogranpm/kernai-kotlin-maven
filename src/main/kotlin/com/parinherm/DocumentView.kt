package com.parinherm

import org.eclipse.jface.text.Document
import org.eclipse.jface.text.IDocument
import org.eclipse.jface.text.source.*
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Composite

class DocumentView (parent: Composite) : Composite(parent, SWT.NONE) {

    init {
        val document: IDocument = Document()
        document.set("big deal here five guys")
        layout = FillLayout(SWT.VERTICAL)
        val VERTICAL_RULER_WIDTH = 12
        val overviewRuler = OverviewRuler(null, VERTICAL_RULER_WIDTH, null)
        val ruler = CompositeRuler(VERTICAL_RULER_WIDTH)
        val annotationModel = AnnotationModel()
        annotationModel.connect(document)
        val txtBody = SourceViewer(this, ruler, overviewRuler, false, SWT.BORDER or SWT.MULTI or SWT.V_SCROLL or SWT.H_SCROLL)
        txtBody.configure(SourceViewerConfiguration())
        txtBody.setDocument(document, annotationModel)
        ruler.addDecorator(0, LineNumberRulerColumn())
    }


}
/*
class where the configuration for the source code viewer is set up
adding things like presentation stuff (reconciler)
content assist etc
 */

package com.parinherm.form.widgets

import org.eclipse.jface.text.IAutoIndentStrategy
import org.eclipse.jface.text.IDocument
import org.eclipse.jface.text.contentassist.IContentAssistant
import org.eclipse.jface.text.presentation.IPresentationReconciler
import org.eclipse.jface.text.presentation.PresentationReconciler
import org.eclipse.jface.text.rules.DefaultDamagerRepairer
import org.eclipse.jface.text.source.IAnnotationHover
import org.eclipse.jface.text.source.ISourceViewer
import org.eclipse.jface.text.source.SourceViewerConfiguration

class KotlinSourceViewerConfiguration : SourceViewerConfiguration() {

    override fun getPresentationReconciler(sourceViewer: ISourceViewer?): IPresentationReconciler {
        val reconciler = PresentationReconciler()
        val dr = DefaultDamagerRepairer(KeywordCodeScanner())
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE)
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE)
        return reconciler
    }



}
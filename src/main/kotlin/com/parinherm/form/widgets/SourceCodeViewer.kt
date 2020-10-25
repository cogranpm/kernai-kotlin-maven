package com.parinherm.form.widgets

import org.eclipse.jface.text.source.CompositeRuler
import org.eclipse.jface.text.source.OverviewRuler
import org.eclipse.jface.text.source.SourceViewer
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

}
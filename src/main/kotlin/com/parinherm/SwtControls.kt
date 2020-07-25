package com.parinherm

import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Control
import org.eclipse.swt.widgets.Label

fun getLabel(caption: String, parent: Composite ) : Label {
    val label: Label = Label(parent, SWT.NONE)
    label.text = caption
    return label
}

fun clearComposite(composite: Composite) {
    for(child: Control in composite.children){
        child.dispose()
    }
}



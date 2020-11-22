package com.parinherm.view.filter

import org.eclipse.jface.viewers.ViewerFilter
import org.eclipse.swt.widgets.Text

abstract class BaseViewFilter : ViewerFilter() {
    abstract var searchFields: Map<String, Text>?
}
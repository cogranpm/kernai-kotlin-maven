package com.parinherm.view.filter

import org.eclipse.jface.viewers.ViewerFilter

abstract class BaseViewFilter : ViewerFilter() {
    abstract var searchText: String
}
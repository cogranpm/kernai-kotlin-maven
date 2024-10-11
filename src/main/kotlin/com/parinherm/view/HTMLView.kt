package com.parinherm.view

import com.parinherm.ApplicationData
import org.eclipse.swt.SWT
import org.eclipse.swt.browser.Browser
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Composite

class HTMLView (parent: Composite) : Composite(parent, ApplicationData.swnone) {

    init {
        try {
            layout = FillLayout(SWT.VERTICAL)
            val browser = Browser(this, SWT.EDGE)
            //browser.url = "www.ibm.com"
            browser.text = "<html><body><h1>hello world</h1></body></html>"
            layout()
        } catch (e: Exception){
            ApplicationData.logError(e, "Could not run Edge Browser")
        }

    }

}
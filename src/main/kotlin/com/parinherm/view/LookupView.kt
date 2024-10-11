package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Lookup
import com.parinherm.entity.Recipe
import com.parinherm.form.Form
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.form.makeHeaderText
import com.parinherm.server.DefaultViewDefinitions
import org.eclipse.swt.widgets.Composite

class LookupView(val parent: Composite, comparator: BeansViewerComparator) : View <Lookup> {

    override val form: Form<Lookup> = Form(parent, DefaultViewDefinitions.loadView(ViewDefConstants.lookupViewId), comparator)

    init {
        val introText = """
            Lookups are how you provide values for Combo boxes. Examples might be Country or State lookups.
        """.trimIndent()
        makeHeaderText(form.headerSection, introText)
    }
}
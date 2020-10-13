package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Recipe
import com.parinherm.entity.Snippet
import com.parinherm.form.Form
import org.eclipse.swt.widgets.Composite

class SnippetView (val parent: Composite, comparator: BeansViewerComparator) : View <Snippet>{
    val formDef: Map<String, Any> =
            ApplicationData.getView(
                    ApplicationData.ViewDef.recipeViewId,
                    ApplicationData.viewDefinitions
            )

    // this member has all of the widgets
    // it's a common object favour composition over inheritance
    override val form: Form<Snippet> = Form(parent, formDef, comparator)

}
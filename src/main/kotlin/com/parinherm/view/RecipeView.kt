package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Person
import com.parinherm.entity.Recipe
import com.parinherm.form.Form
import org.eclipse.swt.widgets.Composite

class RecipeView(val parent: Composite, comparator: BeansViewerComparator)
    : View <Recipe> {

        val formDef: Map<String, Any> =
            ApplicationData.getView(
                ApplicationData.ViewDef.recipeViewId,
                ApplicationData.viewDefinitions
            )

        // this member has all of the widgets
        // it's a common object favour composition over inheritance
        override val form: Form<Recipe> = Form(parent, formDef, comparator)

        init {
        }


}
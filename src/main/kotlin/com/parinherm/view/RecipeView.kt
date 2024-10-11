package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Recipe
import com.parinherm.form.Form
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.server.DefaultViewDefinitions
import com.parinherm.view.filter.RecipeViewFilter
import org.eclipse.swt.widgets.Composite

class RecipeView(val parent: Composite, comparator: BeansViewerComparator)
    : View <Recipe> {

        // this member has all of the widgets
        // it's a common object favour composition over inheritance
        override val form: Form<Recipe> = Form(parent,
            DefaultViewDefinitions.loadView(ViewDefConstants.recipeViewId),
            comparator,
            RecipeViewFilter()
        )

}
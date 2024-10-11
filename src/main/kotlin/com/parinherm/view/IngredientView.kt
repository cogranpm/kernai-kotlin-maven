package com.parinherm.view

import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Ingredient
import com.parinherm.form.Form
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.server.DefaultViewDefinitions
import org.eclipse.swt.widgets.Composite

class IngredientView(val parent: Composite, comparator: BeansViewerComparator) : View<Ingredient> {

    // this member has all of the widgets
    // it's a common object favour composition over inheritance
    override val form: Form<Ingredient> = Form(parent, DefaultViewDefinitions.loadView(ViewDefConstants.ingredientViewId), comparator)
    init {
    }

}
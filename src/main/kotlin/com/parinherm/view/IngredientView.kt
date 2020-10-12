package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Ingredient
import com.parinherm.form.Form
import org.eclipse.swt.widgets.Composite

class IngredientView(val parent: Composite, comparator: BeansViewerComparator) : View<Ingredient> {

    private val formDef: Map<String, Any> =
            ApplicationData.getView(
                    ApplicationData.ViewDef.ingredientViewId,
                    ApplicationData.viewDefinitions
            )

    // this member has all of the widgets
    // it's a common object favour composition over inheritance
    override val form: Form<Ingredient> = Form(parent, formDef, comparator)

}
package com.parinherm.viewmodel

import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.Recipe
import com.parinherm.entity.schema.RecipeMapper
import com.parinherm.form.FormViewModel
import com.parinherm.view.RecipeView
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder

class RecipeViewModel(parent: CTabFolder) : FormViewModel<Recipe>(
    RecipeView(parent, Comparator()),
    RecipeMapper, { Recipe.make() }) {

    init {
        loadData(mapOf())
    }


    class Comparator : BeansViewerComparator(), IViewerComparator {

        val name_index = 0
        val method_index = 1

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Recipe
            val entity2 = e2 as Recipe
            val rc = when (propertyIndex) {
                name_index -> entity1.name.compareTo(entity2.name)
                method_index -> entity1.method.compareTo(entity2.method)
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }


}
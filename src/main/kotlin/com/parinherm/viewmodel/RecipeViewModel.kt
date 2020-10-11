package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.PersonDetail
import com.parinherm.entity.Recipe
import com.parinherm.entity.schema.PersonDetailMapper
import com.parinherm.form.FormViewModel
import com.parinherm.view.PersonDetailView
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder

class RecipeViewModel(parent: CTabFolder) : FormViewModel<Recipe>(
    RecipeView(parent, RecipeViewModel.Comparator()),
    RecipeMapper, { Recipe.make() }) {


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
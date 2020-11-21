package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.Ingredient
import com.parinherm.entity.Recipe
import com.parinherm.entity.schema.IngredientMapper
import com.parinherm.entity.schema.RecipeMapper
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.view.RecipeView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder

class RecipeViewModel(parent: CTabFolder) : FormViewModel<Recipe>(
    RecipeView(parent, Comparator()),
    RecipeMapper, { Recipe.make() }) {

    private val ingredients = WritableList<Ingredient>()
    private val ingredientsComparator = IngredientViewModel.Comparator()

    init {
        if (view.form.childFormsContainer != null)
        {
            view.form.childFormsContainer!!.childTabs.forEach { childFormTab: ChildFormTab ->
                wireChildTab(childFormTab, ApplicationData.TAB_KEY_INGREDIENT, ingredientsComparator, ingredients, ::makeIngredientsViewModel)
            }
        }
        loadData(mapOf())
    }

    private fun makeIngredientsViewModel(currentChild: Ingredient?) : IFormViewModel<Ingredient> {
        return IngredientViewModel(
            currentEntity!!.id,
            currentChild,
            ApplicationData.TAB_KEY_RECIPE,
            ApplicationData.mainWindow.folder)
    }

    override fun changeSelection(){
        val formBindings = super.changeSelection()
        /* specific to child list */
        ingredients.clear()
        ingredients.addAll(IngredientMapper.getAll(mapOf("recipeId" to currentEntity!!.id)))
    }

    override fun refresh() {
        super.refresh()
        ingredients.clear()
        ingredients.addAll(IngredientMapper.getAll(mapOf("recipeId" to currentEntity!!.id)))
    }



    class Comparator : BeansViewerComparator(), IViewerComparator {

        val name_index = 0
        //val method_index = 1
        val category_index = 1

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Recipe
            val entity2 = e2 as Recipe
            val rc = when (propertyIndex) {
                name_index -> compareString(entity1.name, entity2.name)
                category_index -> compareLookups(entity1.category, entity2.category, ApplicationData.recipeCategoryList)
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }


}
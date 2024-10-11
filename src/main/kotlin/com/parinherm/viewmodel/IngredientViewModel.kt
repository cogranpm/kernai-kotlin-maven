package com.parinherm.viewmodel

import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.Ingredient
import com.parinherm.entity.Recipe
import com.parinherm.entity.schema.IngredientMapper
import com.parinherm.form.FormViewModel
import com.parinherm.form.makeHeaderText
import com.parinherm.lookups.LookupUtils
import com.parinherm.menus.TabInfo
import com.parinherm.view.IngredientView
import org.eclipse.jface.viewers.Viewer

class IngredientViewModel (
    val recipe: Recipe?,
    val selectedIngredient: Ingredient?,
    val openedFromTabId: String?,
    tabInfo: TabInfo):
        FormViewModel<Ingredient>(IngredientView(tabInfo.folder, IngredientViewModel.Comparator()),
                IngredientMapper, { Ingredient.make(recipe!!.id)}, tabInfo)  {

    init {
        loadData(mapOf("recipeId" to recipe!!.id))
        onLoad(selectedIngredient)
        if(recipe != null){
           makeHeaderText(this.view.form.headerSection, "Recipe: ${recipe.name}")
        }
    }

    override fun getData(parameters: Map<String, Any>): List<Ingredient> {
        return mapper.getAll(parameters as Map<String, Long>)
    }

    override fun save() {
        super.save()
        afterSave(openedFromTabId)
    }


    class Comparator : BeansViewerComparator(), IViewerComparator {

        val name_index = 0
        val quantity_index = 1
        val unit_index = 2

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Ingredient
            val entity2 = e2 as Ingredient
            val rc = when(propertyIndex){
                name_index -> compareString(entity1.name, entity2.name)
                quantity_index -> entity1.quantity.compareTo(entity2.quantity)
                unit_index -> compareLookups(entity1.unit, entity2.unit, LookupUtils.getLookupByKey(LookupUtils.unitLookupKey, false))
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }

}
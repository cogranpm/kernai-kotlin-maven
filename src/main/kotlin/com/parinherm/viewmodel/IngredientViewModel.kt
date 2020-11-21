package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.Ingredient
import com.parinherm.entity.schema.IngredientMapper
import com.parinherm.form.FormViewModel
import com.parinherm.view.IngredientView
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder

class IngredientViewModel (val recipeId: Long, val selectedIngredient: Ingredient?, val openedFromTabId: String?, parent: CTabFolder):
        FormViewModel<Ingredient>(IngredientView(parent, IngredientViewModel.Comparator()),
                IngredientMapper, { Ingredient.make(recipeId)})  {


    init {
        loadData(mapOf("recipeId" to recipeId))
        onLoad(selectedIngredient)
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
                unit_index -> compareLookups(entity1.unit, entity2.unit, ApplicationData.unitList)
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }

}
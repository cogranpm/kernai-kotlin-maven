package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.Ingredient
import com.parinherm.entity.PersonDetail
import com.parinherm.entity.Recipe
import com.parinherm.entity.schema.IngredientMapper
import com.parinherm.entity.schema.Ingredients
import com.parinherm.entity.schema.PersonDetailMapper
import com.parinherm.entity.schema.RecipeMapper
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.form.makeViewerLabelProvider
import com.parinherm.view.RecipeView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.events.SelectionListener

class RecipeViewModel(parent: CTabFolder) : FormViewModel<Recipe>(
    RecipeView(parent, Comparator()),
    RecipeMapper, { Recipe.make() }) {

    val ingredients = WritableList<Ingredient>()
    val ingredientsComparator = IngredientViewModel.Comparator()
    val ingredientsContentProvider = ObservableListContentProvider<Ingredient>()

    init {
        if (view.form.childFormsContainer != null)
        {
            view.form.childFormsContainer!!.childTabs.forEach { childFormTab: ChildFormTab ->
                wireChildEntity(childFormTab)
            }
        }
        loadData(mapOf())
    }


    private fun wireChildEntity(childFormTab: ChildFormTab) : Unit {
        val fields = childFormTab.childDefinition[ApplicationData.ViewDef.fields] as List<Map<String, Any>>
        val title = childFormTab.childDefinition[ApplicationData.ViewDef.title] as String

        childFormTab.listView.contentProvider = ingredientsContentProvider
        childFormTab.listView.labelProvider = makeViewerLabelProvider<Ingredient>(fields, ingredientsContentProvider.knownElements)
        childFormTab.listView.comparator = ingredientsComparator
        childFormTab.listView.input = ingredients


        childFormTab.listView.addOpenListener {
            // open up a tab to edit child entity
            val selection = childFormTab.listView.structuredSelection
            val selectedItem = selection.firstElement
            // store the selected item in the list in the viewstate
            val currentIngredient = selectedItem as Ingredient
            openIngredientsTab(currentIngredient, title)
        }

        childFormTab.btnAdd.addSelectionListener(SelectionListener.widgetSelectedAdapter { _ ->
            openIngredientsTab(null, title)
        })

        listHeaderSelection(childFormTab.listView, childFormTab.columns, ingredientsComparator)
    }

    fun openIngredientsTab(currentIngredient: Ingredient?, title: String){
        val viewModel: IFormViewModel<Ingredient> = IngredientViewModel(currentEntity!!.id,
                currentIngredient,
                ApplicationData.TAB_KEY_RECIPE,
                ApplicationData.mainWindow.folder)
        ApplicationData.makeTab(viewModel, title, ApplicationData.TAB_KEY_INGREDIENT)
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
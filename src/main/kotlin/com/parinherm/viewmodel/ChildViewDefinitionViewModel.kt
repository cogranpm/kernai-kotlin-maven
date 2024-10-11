package com.parinherm.viewmodel

import com.parinherm.entity.FieldDefinition
import com.parinherm.entity.ViewDefinition
import com.parinherm.entity.schema.FieldDefinitionMapper
import com.parinherm.entity.schema.ViewDefinitionMapper
import com.parinherm.form.*
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.menus.TabInfo
import com.parinherm.server.DefaultViewDefinitions
import com.parinherm.view.ChildViewDefinitionView
import org.eclipse.core.databinding.observable.list.WritableList
import java.util.*


class ChildViewDefinitionViewModel(
    val viewDefinition: ViewDefinition,
    val selectedViewDefinition: ViewDefinition?,
    val openedFromTabId: String?,
    tabInfo: TabInfo
) : FormViewModel<ViewDefinition>(
    ChildViewDefinitionView(tabInfo.folder, ViewDefinitionViewModel.Comparator()),
    ViewDefinitionMapper,
    { ViewDefinition.make(viewDefinition.id) }, tabInfo
) {

    private val fieldDefinitions = WritableList<FieldDefinition>()
    private val fieldDefinitionComparator = FieldDefinitionViewModel.Comparator()
    private val childViewDefinitions = WritableList<ViewDefinition>()

    init {
        loadData(mapOf("viewDefinitionId" to viewDefinition.id))
        makeHeaderText(this.view.form.headerSection, "Parent Form Definition: ${viewDefinition.viewId}")
        onLoad(selectedViewDefinition)

        /* manually add self referencing child tab */
        val childTab = makeChildTab(
            view.form.childFormsContainer!!.folder,
            DefaultViewDefinitions.loadView(ViewDefConstants.viewDefinitionViewId)
        )

        wireChildTab(
            childTab,
            ViewDefinitionViewModel.Comparator(),
            childViewDefinitions,
            ::makeChildDefinitionsViewModel,
            ViewDefinitionMapper
        )


        view.form.childFormsContainer!!.childTabs.forEach { childFormTab: ChildFormTab ->
            when (childFormTab.key.lowercase(Locale.getDefault())) {
                ViewDefConstants.fieldDefinitionViewId -> wireChildTab(
                    childFormTab,
                    fieldDefinitionComparator,
                    fieldDefinitions, ::makeFieldDefinitionsViewModel,
                    FieldDefinitionMapper
                )
            }
        }
    }

    override fun getData(parameters: Map<String, Any>): List<ViewDefinition> {
        return (mapper as ViewDefinitionMapper).getAllByParent(parameters as Map<String, Long>)
    }

    override fun save() {
        super.save()
        afterSave(openedFromTabId)
    }

    private fun makeFieldDefinitionsViewModel(currentChild: FieldDefinition?): IFormViewModel<FieldDefinition> {
        return FieldDefinitionViewModel(
            currentEntity!!,
            currentChild,
            tabId,
            tabInfo.copy(caption = "Field Definition")
        )
    }

    private fun makeChildDefinitionsViewModel(currentChild: ViewDefinition?): IFormViewModel<ViewDefinition> {
        return ChildViewDefinitionViewModel(
            currentEntity!!,
            currentChild,
            tabId,
            tabInfo.copy(caption = "Child Form Definition")
        )
    }

    private fun clearAndAddFieldDefinition() {
        fieldDefinitions.clear()
        fieldDefinitions.addAll(FieldDefinitionMapper.getAll(mapOf("viewDefinitionId" to currentEntity!!.id)))
    }

    private fun clearAndAddChildViewDefinition() {
        childViewDefinitions.clear()
        childViewDefinitions.addAll(ViewDefinitionMapper.getAllByParent(mapOf("viewDefinitionId" to currentEntity!!.id)))
    }

    override fun changeSelection() {
        val formBindings = super.changeSelection()
        /* specific to child list */
        clearAndAddFieldDefinition()
        clearAndAddChildViewDefinition()
    }

    override fun refresh() {
        super.refresh()
        clearAndAddFieldDefinition()
        clearAndAddChildViewDefinition()
    }

}
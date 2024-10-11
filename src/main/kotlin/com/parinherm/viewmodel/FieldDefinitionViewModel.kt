package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.*
import com.parinherm.entity.schema.*
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.form.makeHeaderText
import com.parinherm.lookups.LookupUtils
import com.parinherm.menus.TabInfo
import com.parinherm.view.FieldDefinitionView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder

class FieldDefinitionViewModel(
    val viewDefinition: ViewDefinition,
    val selectedFieldDefinition: FieldDefinition?,
    val openedFromTabId: String?,
    tabInfo: TabInfo) : FormViewModel<FieldDefinition>(
    FieldDefinitionView(tabInfo.folder, Comparator()),
    FieldDefinitionMapper,
    { FieldDefinition.make(viewDefinition.id) },
    tabInfo) {

    init {
        loadData(mapOf("viewDefinitionId" to viewDefinition.id))
        makeHeaderText(this.view.form.headerSection, "Parent Form Definition: ${viewDefinition.viewId}")
        onLoad(selectedFieldDefinition)
    }

    override fun getData(parameters: Map<String, Any>): List<FieldDefinition> {
        return mapper.getAll(parameters as Map<String, Long>)
    }

    override fun save() {
        super.save()
        afterSave(openedFromTabId)
    }

    class Comparator : BeansViewerComparator(), IViewerComparator {
        val name_index = 0
        val title_index = 1
        val required_index = 2
        val size_index = 3
        val dataType_index = 4
        val lookupKey_index = 5

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as FieldDefinition
            val entity2 = e2 as FieldDefinition
            val rc = when (propertyIndex) {
                name_index -> compareString(entity1.name, entity2.name)
                title_index -> compareString(entity1.title, entity2.title)
                required_index -> entity1.required.compareTo(entity2.required)
                size_index -> compareLookups(entity1.size, entity2.size, LookupUtils.getLookupByKey(LookupUtils.fieldSizeLookupKey, false))
                dataType_index -> compareLookups(entity1.dataType, entity2.dataType, LookupUtils.getLookupByKey(LookupUtils.dataTypeLookupKey, false))
                lookupKey_index -> compareString(entity1.lookupKey ?: "", entity2.lookupKey ?: "")
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }


}
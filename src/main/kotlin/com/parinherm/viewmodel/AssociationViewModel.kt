package com.parinherm.viewmodel

import com.parinherm.entity.AssociationDefinition
import com.parinherm.entity.schema.AssociationDefinitionMapper
import com.parinherm.form.FormViewModel
import com.parinherm.menus.TabInfo
import com.parinherm.view.AssociationView
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.lookups.LookupUtils
import org.eclipse.jface.viewers.Viewer

class AssociationViewModel(tabInfo: TabInfo) : FormViewModel<AssociationDefinition> (
    AssociationView(tabInfo.folder, Comparator()),
    AssociationDefinitionMapper, {AssociationDefinition.make()},
    tabInfo
) {

    init {
        createTab()
        loadData(mapOf())
    }

    class Comparator : BeansViewerComparator(), IViewerComparator {

        val name_index = 0

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as AssociationDefinition
            val entity2 = e2 as AssociationDefinition
            val rc = when (propertyIndex) {
                name_index -> compareString(entity1.name, entity2.name)
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }

}
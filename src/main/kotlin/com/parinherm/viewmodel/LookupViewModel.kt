package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.Login
import com.parinherm.entity.Lookup
import com.parinherm.entity.schema.LookupMapper
import com.parinherm.form.FormViewModel
import com.parinherm.view.LookupView
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder

class LookupViewModel (parent: CTabFolder)  : FormViewModel<Lookup> (
        LookupView(parent, Comparator()),
        LookupMapper, { Lookup.make()}
) {

    class Comparator : BeansViewerComparator(), IViewerComparator {

        val key_index = 0
        val label_index = 1

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Lookup
            val entity2 = e2 as Lookup
            val rc = when (propertyIndex) {
                key_index -> entity1.key.compareTo(entity2.key)
                label_index -> entity1.label.compareTo(entity2.label)
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }

}
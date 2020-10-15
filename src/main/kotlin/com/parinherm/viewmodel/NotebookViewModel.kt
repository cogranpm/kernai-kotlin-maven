package com.parinherm.viewmodel

import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.Notebook
import com.parinherm.entity.schema.NotebookMapper
import com.parinherm.form.FormViewModel
import com.parinherm.view.NotebookView
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder

class NotebookViewModel  (parent: CTabFolder)  : FormViewModel<Notebook>(
    NotebookView(parent, NotebookViewModel.Comparator()),
    NotebookMapper, { Notebook.make() })  {


    init {
        loadData(mapOf())
    }

    class Comparator : BeansViewerComparator(), IViewerComparator {

        val name_index = 0

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Notebook
            val entity2 = e2 as Notebook
            val rc = when (propertyIndex) {
                name_index -> entity1.name.toLowerCase().compareTo(entity2.name.toLowerCase())
               else -> 0
            }
            return flipSortDirection(rc)
        }
    }

}
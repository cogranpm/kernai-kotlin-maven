package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.*
import com.parinherm.entity.schema.*
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.view.ShelfView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder

class ShelfViewModel( parent: CTabFolder) : FormViewModel<Shelf>(
    ShelfView(parent, Comparator()),
    ShelfMapper,
    { Shelf.make() }) {

    
    private val subjects = WritableList<Subject>()
    private val subjectComparator = SubjectViewModel.Comparator()
    

    init {

        if (view.form.childFormsContainer != null)
        {
            view.form.childFormsContainer!!.childTabs.forEach {
                childFormTab: ChildFormTab ->
        
                    wireChildTab(childFormTab, ApplicationData.TAB_KEY_SUBJECT, subjectComparator, subjects, ::makeSubjectsViewModel)
        
            }
        }

        loadData(mapOf())
        
    }






    private fun makeSubjectsViewModel(currentChild: Subject?) : IFormViewModel<Subject> {
        return SubjectViewModel(
        currentEntity!!.id,
        currentChild,
        ApplicationData.TAB_KEY_SHELF,
        ApplicationData.mainWindow.folder)
        }

    private fun clearAndAddSubject() {
        subjects.clear()
        subjects.addAll(SubjectMapper.getAll(mapOf("shelfId" to currentEntity!!.id)))
    }


    override fun changeSelection(){
        val formBindings = super.changeSelection()
        /* specific to child list */

        clearAndAddSubject()

    }

    override fun refresh() {
        super.refresh()

        clearAndAddSubject()

    }




    class Comparator : BeansViewerComparator(), IViewerComparator {


        val title_index = 0

        val comments_index = 1

        val createdDate_index = 2


        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Shelf
            val entity2 = e2 as Shelf
            val rc = when (propertyIndex) {

                title_index -> compareString(entity1.title, entity2.title)

                comments_index -> compareString(entity1.comments, entity2.comments)

                createdDate_index -> entity1.createdDate.compareTo(entity2.createdDate)

                else -> 0
            }
            return flipSortDirection(rc)
        }
    }


}
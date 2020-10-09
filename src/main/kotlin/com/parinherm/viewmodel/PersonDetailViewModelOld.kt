package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.TabInstance
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.ModelBinder
import com.parinherm.entity.PersonDetail
import com.parinherm.entity.schema.PersonDetailMapper
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.jface.viewers.TableViewer
import org.eclipse.swt.widgets.Composite

class PersonDetailViewModelOld(val beanTestId: Long,
                               val selectedItem: PersonDetail?,
                               val parentTabInstance: TabInstance?,
                               data: List<PersonDetail>,
                               comparator: BeansViewerComparator,
                               modelBinder: ModelBinder<PersonDetail>
) : ViewModel<PersonDetail>(data, comparator, modelBinder, PersonDetailMapper) {

    override fun render(parent: Composite, viewDefinition: Map<String, Any>): Composite {
        val composite = super.render(parent, viewDefinition)
        val fields = viewDefinition[ApplicationData.ViewDef.fields] as List<Map<String, Any>>
        if (selectedItem != null) {
            val listView = getWidget("list") as TableViewer
            val itemInWritableList = wl.find { it.id == selectedItem.id }
            if (itemInWritableList != null) {
                listView.selection = StructuredSelection(itemInWritableList)
            }
        } else {
            applyNewCommand(fields)
        }
        return composite
    }


    override fun afterListSelection(listView: TableViewer, currentItem: PersonDetail) {

    }

    override fun makeNewEntity(): PersonDetail {
        return PersonDetail.make(beanTestId)
    }

}
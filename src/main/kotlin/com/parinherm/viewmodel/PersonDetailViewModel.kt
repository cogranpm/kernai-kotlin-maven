package com.parinherm.viewmodel

import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.ModelBinder
import com.parinherm.entity.BeanTest
import com.parinherm.entity.PersonDetail
import org.eclipse.jface.viewers.TableViewer

class PersonDetailViewModel(val beanTestId: Long, data: List<PersonDetail>,
                            entity_maker: ()-> PersonDetail,
                            comparator: BeansViewerComparator,
                            modelBinder: ModelBinder<PersonDetail>
) : ViewModel<PersonDetail>(data, entity_maker, comparator, modelBinder) {


    override fun afterListSelection(listView: TableViewer, currentItem: PersonDetail) {

    }

}
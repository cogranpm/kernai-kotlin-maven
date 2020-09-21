/****************************
 *  is the view model class for the BeanTest entity
 *  manages the state for the view and all the events/commands and such
 *  state will consist of a list of BeanTest entities
 *  the current BeanTest entity, ie the one selected in the list
 *  ah ha, should really just manage this via a jface structured selection property
 *  that would be easier
  */

package com.parinherm.viewmodel

import com.parinherm.builders.BeansViewState
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.ModelBinder
import com.parinherm.entity.BeanTest


class BeanTestViewModel(data: List<BeanTest>, bean_maker: ()-> BeanTest, comparator: BeansViewerComparator, modelBinder: ModelBinder<BeanTest>)
    : BeansViewState<BeanTest> (data, bean_maker, comparator, modelBinder){
}
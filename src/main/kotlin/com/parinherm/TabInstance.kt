package com.parinherm

import com.parinherm.entity.IBeanDataEntity
import com.parinherm.viewmodel.ViewModel
import org.eclipse.swt.widgets.TabItem

data class TabInstance (val viewModel: ViewModel<*>, val tab: TabItem){

}
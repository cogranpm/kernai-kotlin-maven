package com.parinherm

import com.parinherm.viewmodel.IViewModel
import com.parinherm.viewmodel.ViewModel
import org.eclipse.swt.custom.CTabItem

data class TabInstance (val viewModel: IViewModel, val tab: CTabItem, var isClosed: Boolean){

}
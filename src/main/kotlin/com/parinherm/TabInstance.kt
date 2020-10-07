package com.parinherm

import com.parinherm.form.IFormViewModel
import org.eclipse.swt.custom.CTabItem

data class TabInstance (val viewModel: IFormViewModel, val tab: CTabItem, var isClosed: Boolean){

}
package com.parinherm.form

import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.widgets.Composite

data class ChildForm (var folder: CTabFolder, var childTabs: List<ChildFormTab>)
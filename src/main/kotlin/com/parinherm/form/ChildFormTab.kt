package com.parinherm.form

import com.parinherm.form.definitions.ViewDef
import org.eclipse.jface.viewers.TableViewer
import org.eclipse.jface.viewers.TableViewerColumn
import org.eclipse.swt.custom.CTabItem
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite

data class ChildFormTab(val key: String,
                        val childDefinition: ViewDef,
                        val tab: CTabItem,
                        val buttonBar: Composite,
                        val btnAdd: Button,
                        val btnDelete: Button,
                        val listContainer: Composite,
                        val listView: TableViewer,
                        val columns: List<TableViewerColumn>) {

}
package com.parinherm.form.dialogs

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.ViewDefinition
import com.parinherm.entity.schema.ViewDefinitionMapper
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.form.getListViewer
import com.parinherm.form.makeColumns
import com.parinherm.form.makeViewerLabelProvider
import com.parinherm.lookups.LookupUtils
import com.parinherm.server.DefaultViewDefinitions
import org.eclipse.core.databinding.BindingProperties.model
import org.eclipse.core.databinding.DataBindingContext
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.databinding.swt.ISWTObservableValue
import org.eclipse.jface.databinding.swt.typed.WidgetProperties
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider
import org.eclipse.jface.dialogs.IMessageProvider
import org.eclipse.jface.dialogs.TitleAreaDialog
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.jface.layout.GridLayoutFactory
import org.eclipse.jface.layout.TableColumnLayout
import org.eclipse.jface.viewers.TableViewer
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.*


class ViewDefinitionSelector(parent: Shell) : TitleAreaDialog(parent){

    private val viewDefinition = DefaultViewDefinitions.loadView(ViewDefConstants.viewDefinitionViewId, false)
    private val dataList = WritableList<ViewDefinition>()
    private lateinit var listView: TableViewer
    var selectedEntity: ViewDefinition? = null
    var selectedEntities: MutableList<ViewDefinition> = mutableListOf()

    override fun create() {
        super.create()
        setTitle("Select Form Definition")
        setMessage("Select the Form Definitions for which the classes will be generated.", IMessageProvider.INFORMATION)
    }

    override fun okPressed() {
        /* lets capture the selection */
        val selection = listView.structuredSelection
        if (!selection.isEmpty) {
            val selectedItem = selection.firstElement
            selectedEntity = selectedItem as ViewDefinition
            for(aSelection in selection.toList()){
                selectedEntities.add(aSelection as ViewDefinition)
            }
        }
        super.okPressed()
    }

    override fun getInitialSize(): Point {
        return Point(640, 480)
    }

    override fun createButtonsForButtonBar(parent: Composite?) {
        super.createButtonsForButtonBar(parent)
    }

    override fun createDialogArea(parent: Composite?): Control {
        val content = Composite(super.createDialogArea(parent) as Composite, ApplicationData.swnone)
        val listContainer = Composite(content, ApplicationData.swnone)
        val tableLayout = TableColumnLayout(true)
        listView = getListViewer(listContainer, tableLayout, true)
        val columns = makeColumns(listView, viewDefinition.fieldDefinitions, tableLayout)
        val contentProvider = ObservableListContentProvider<ViewDefinition>()
        listView.contentProvider = contentProvider
        listView.labelProvider = makeViewerLabelProvider<ViewDefinition>(viewDefinition.fieldDefinitions, contentProvider.knownElements)
        listView.comparator = Comparator()
        dataList.addAll(ViewDefinitionMapper.getAll(mapOf()))
        listView.input = dataList

        GridDataFactory.defaultsFor(listContainer).grab(true, true).hint(150, 150).applyTo(listContainer)
        GridLayoutFactory.fillDefaults().numColumns(1).generateLayout(content)
        GridDataFactory.fillDefaults().grab(true, true).applyTo(content)
        content.layout = FillLayout(SWT.VERTICAL)
        return content
    }


    class Comparator : BeansViewerComparator(), IViewerComparator {

        val viewId_index = 0
        val title_index = 1
        val listWeight_index = 2
        val editWeight_index = 3
        val sashOrientation_index = 4
        val entityName_index = 5

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as ViewDefinition
            val entity2 = e2 as ViewDefinition
            val rc = when (propertyIndex) {

                viewId_index -> compareString(entity1.viewId, entity2.viewId)
                title_index -> compareString(entity1.title, entity2.title)
                listWeight_index -> entity1.listWeight.compareTo(entity2.listWeight)
                editWeight_index -> entity1.editWeight.compareTo(entity2.editWeight)
                sashOrientation_index -> compareLookups(
                    entity1.sashOrientation,
                    entity2.sashOrientation,
                    LookupUtils.getLookupByKey(LookupUtils.sashOrientationLookupKey, false)
                )
                entityName_index -> compareString(entity1.entityName, entity2.entityName)
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }
}
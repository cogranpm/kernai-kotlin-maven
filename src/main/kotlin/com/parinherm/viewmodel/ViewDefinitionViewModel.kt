package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.*
import com.parinherm.entity.schema.*
import com.parinherm.entity.schema.ViewDefinitions.viewId
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.form.makeChildTab
import com.parinherm.lookups.LookupUtils
import com.parinherm.menus.TabInfo
import com.parinherm.script.KotlinScriptRunner
import com.parinherm.model.makeScriptFiles
import com.parinherm.script.GraalScriptRunner
import com.parinherm.server.DefaultViewDefinitions
import com.parinherm.view.ViewDefinitionView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swt.SWT
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Text
import java.lang.Exception
import java.util.*

class ViewDefinitionViewModel(tabInfo: TabInfo) : FormViewModel<ViewDefinition>(
    ViewDefinitionView(tabInfo.folder, Comparator()),
    ViewDefinitionMapper,
    { ViewDefinition.make() },
    tabInfo
) {

    private val fieldDefinitions = WritableList<FieldDefinition>()
    private val fieldDefinitionComparator = FieldDefinitionViewModel.Comparator()
    private val childViewDefinitions = WritableList<ViewDefinition>()

    init {

        if (view.form.childFormsContainer != null) {
            /* add a child tab for the self reference */
            val childTab = makeChildTab(
                view.form.childFormsContainer!!.folder,
                DefaultViewDefinitions.loadView(ViewDefConstants.viewDefinitionViewId)
            )

            wireChildTab(
                childTab,
                Comparator(),
                childViewDefinitions,
                ::makeChildDefinitionsViewModel,
                ViewDefinitionMapper
            )

            view.form.childFormsContainer!!.childTabs.forEach { childFormTab: ChildFormTab ->
                when (childFormTab.key.lowercase(Locale.getDefault())) {

                    ViewDefConstants.fieldDefinitionViewId -> wireChildTab(
                        childFormTab,
                        fieldDefinitionComparator,
                        fieldDefinitions, ::makeFieldDefinitionsViewModel,
                        FieldDefinitionMapper
                    )
                }
            }

        }

        val view = view as ViewDefinitionView
        view.btnMake.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                if (currentEntity != null){
                    val viewId = currentEntity?.viewId
                    if(viewId != null){
                        Display.getDefault().activeShell.cursor = Display.getDefault().getSystemCursor(SWT.CURSOR_WAIT)
                        view.txtProgress.text = "Generating scripts ..."
                        var targetFiles = mutableListOf<String>()
                        MainScope().launch(Dispatchers.SWT) {
                            try {
                                val viewDef = DefaultViewDefinitions.loadView(viewId)
                                targetFiles = makeScriptFiles(listOf(viewDef))
                            } catch (e: Exception) {
                                ApplicationData.logError(e, "Error generating scripts")
                            } finally {
                                var completeMessage = "Complete"
                                if(!targetFiles.isEmpty()){
                                   completeMessage += ", Script written to: ${targetFiles[0]} "
                                }
                                view.txtProgress.text = completeMessage
                                Display.getDefault().activeShell.cursor = null
                            }
                        }
                    }
                }
            }
        })


        view.btnScaffold.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                if (currentEntity != null) {
                    val viewId = currentEntity?.viewId
                    if(viewId != null){
                        Display.getDefault().asyncExec {
                            try {
                                Display.getDefault().activeShell.cursor =
                                    Display.getDefault().getSystemCursor(SWT.CURSOR_WAIT)
                                view.txtProgress.text = "Enhanced scaffolding ..."
                                val viewDef = DefaultViewDefinitions.loadView(viewId)
                                GraalScriptRunner.runScriptFile(view.commandOutput, viewDef)
                            } catch (e: Exception) {
                                Display.getDefault().timerExec(200) {
                                    view.commandOutput.text = """
                                *************  Error *****************
                                ${e.message}
                                ${e.stackTrace}
                            """.trimIndent()
                                }
                            } finally {
                                Display.getDefault().activeShell.cursor = null
                                view.txtProgress.text = "Complete"
                            }
                        }
                    }
                }
            }
        })

        createTab()
        loadData(mapOf())
    }

    private fun makeFieldDefinitionsViewModel(currentChild: FieldDefinition?): IFormViewModel<FieldDefinition> {
        return FieldDefinitionViewModel(
            currentEntity!!,
            currentChild,
            tabId,
           tabInfo.copy(caption = "Field Definition")
        )
    }

    private fun makeChildDefinitionsViewModel(currentChild: ViewDefinition?): IFormViewModel<ViewDefinition> {
        return ChildViewDefinitionViewModel(currentEntity!!,
            currentChild,
            tabId,
            tabInfo.copy(caption = "Child Form Definition") )
    }

    private fun clearAndAddFieldDefinition() {
        fieldDefinitions.clear()
        fieldDefinitions.addAll(FieldDefinitionMapper.getAll(mapOf("viewDefinitionId" to currentEntity!!.id)))
    }

    private fun clearAndAddChildViewDefinition() {
        childViewDefinitions.clear()
        childViewDefinitions.addAll(ViewDefinitionMapper.getAllByParent(mapOf("viewDefinitionId" to currentEntity!!.id)))
    }

    override fun changeSelection() {
        val formBindings = super.changeSelection()
        /* specific to child list */
        clearAndAddFieldDefinition()
        clearAndAddChildViewDefinition()
    }

    override fun refresh() {
        super.refresh()
        clearAndAddFieldDefinition()
        clearAndAddChildViewDefinition()
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
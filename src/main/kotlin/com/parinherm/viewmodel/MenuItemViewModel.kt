package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator

import com.parinherm.entity.MenuItem
import com.parinherm.entity.schema.MenuItemMapper
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.lookups.LookupUtils
import com.parinherm.menus.TabInfo
import com.parinherm.view.MenuItemView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder


class MenuItemViewModel( val menuId: Long,
                         val selectedMenuItem: MenuItem?,
                         val openedFromTabId: String?,
                         tabInfo: TabInfo
                         ) : FormViewModel<MenuItem>(
    MenuItemView(tabInfo.folder, MenuItemComparator()),
    MenuItemMapper,
    { MenuItem.make(menuId ) },
    tabInfo
) {

    init {

        loadData(mapOf("menuId" to menuId))
        onLoad(selectedMenuItem)
    }

    override fun getData(parameters: Map<String, Any>): List<MenuItem> {
        return mapper.getAll(parameters as Map<String, Long>)
    }

    override fun save() {
        super.save()
        afterSave(openedFromTabId)
        ApplicationData.mainWindow.refreshMenus()
    }

    class MenuItemComparator : BeansViewerComparator(), IViewerComparator {


        val text_index = 0

        val tabCaption_index = 1

        val modifierKey_index = 2

        val acceleratorKey_index = 3

        val viewId_index = 4

        val scriptPath_index = 5

        val font_index = 6

        val image_index = 7


        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as MenuItem
            val entity2 = e2 as MenuItem
            val rc = when (propertyIndex) {

                text_index -> compareString(entity1.text, entity2.text)

                tabCaption_index -> compareString(entity1.tabCaption, entity2.tabCaption)

                modifierKey_index -> compareLookups(entity1.modifierKey, entity2.modifierKey, LookupUtils.getLookupByKey(LookupUtils.modifierKeyLookupKey, true))

                acceleratorKey_index -> compareString(entity1.acceleratorKey, entity2.acceleratorKey)

                viewId_index -> compareString(entity1.viewId, entity2.viewId)

                scriptPath_index -> compareString(entity1.scriptPath, entity2.scriptPath)

                font_index -> compareLookups(entity1.font, entity2.font, LookupUtils.getLookupByKey(LookupUtils.fontLookupKey, true))

                image_index -> compareLookups(entity1.image, entity2.image, LookupUtils.getLookupByKey(LookupUtils.imageLookupKey, true))

                else -> 0
            }
            return flipSortDirection(rc)
        }
    }


}

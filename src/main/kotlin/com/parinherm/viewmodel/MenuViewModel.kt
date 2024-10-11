package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.schema.MenuItemMapper
import com.parinherm.entity.MenuItem
import com.parinherm.entity.Menu
import com.parinherm.entity.schema.MenuMapper
import com.parinherm.form.ChildFormTab
import com.parinherm.form.FormViewModel
import com.parinherm.form.IFormViewModel
import com.parinherm.menus.MenuUtils
import com.parinherm.menus.TabInfo
import com.parinherm.view.MenuView
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder

class MenuViewModel(tabInfo: TabInfo) : FormViewModel<Menu>(
    MenuView(tabInfo.folder, MenuComparator()),
    MenuMapper,
    { Menu.make() },
    tabInfo
) {


    private val menuItems = WritableList<MenuItem>()
    private val menuItemComparator = MenuItemViewModel.MenuItemComparator()


    init {

        if (view.form.childFormsContainer != null) {
            view.form.childFormsContainer!!.childTabs.forEach { childFormTab: ChildFormTab ->
                when (childFormTab.key) {
                    "menuitem" -> wireChildTab(
                        childFormTab,
                        menuItemComparator,
                        menuItems,
                        ::makeMenuItemsViewModel,
                        MenuItemMapper
                    )
                }
            }
        }
        createTab()
        loadData(mapOf())
    }

    private fun makeMenuItemsViewModel(currentChild: MenuItem?): IFormViewModel<MenuItem> {
        return MenuItemViewModel(
            currentEntity!!.id,
            currentChild,
            this.tabId,
            this.tabInfo.copy(caption = "Menu Item")
        )
    }

    private fun clearAndAddMenuItem() {
        menuItems.clear()
        menuItems.addAll(MenuItemMapper.getAll(mapOf("menuId" to currentEntity!!.id)))
    }

    override fun changeSelection() {
        val formBindings = super.changeSelection()
        /* specific to child list */

        clearAndAddMenuItem()

    }

    override fun refresh() {
        super.refresh()

        clearAndAddMenuItem()

    }

    override fun save() {
        super.save()
        ApplicationData.mainWindow.refreshMenus()
    }


    class MenuComparator : BeansViewerComparator(), IViewerComparator {


        val text_index = 0


        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Menu
            val entity2 = e2 as Menu
            val rc = when (propertyIndex) {

                text_index -> compareString(entity1.text, entity2.text)

                else -> 0
            }
            return flipSortDirection(rc)
        }
    }


}

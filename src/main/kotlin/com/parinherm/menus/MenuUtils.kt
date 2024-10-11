package com.parinherm.menus

import com.parinherm.ApplicationData
import com.parinherm.MainWindow
import com.parinherm.entity.Menu
import com.parinherm.entity.MenuItem
import com.parinherm.entity.schema.MenuItemMapper
import com.parinherm.entity.schema.MenuMapper
import com.parinherm.image.ImageUtils
import com.parinherm.script.KotlinScriptRunner
import org.eclipse.jface.action.Action
import org.eclipse.jface.action.MenuManager
import org.eclipse.jface.action.Separator
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.BusyIndicator
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.widgets.Display

object MenuUtils {

    val menuItemScriptRunner = {tabInfo: TabInfo, menuItem: MenuItem ->
        try {
            BusyIndicator.showWhile(Display.getDefault()) {
                KotlinScriptRunner.runScriptFromMenuItem(tabInfo, menuItem)
            }
        } catch (e: Exception) {
            ApplicationData.logError(e, "Error running the script")
            ApplicationData.showErrorDialog("There was an error running menu script", e)
        }
    }

    fun makeMenus(win: MainWindow, menuManager: MenuManager, pullFromDatabase: Boolean){
        val menus: MutableList<Menu> = mutableListOf()
        if(pullFromDatabase){
            val customMenus = MenuMapper.getAll(mapOf())
            menus.addAll(customMenus)
        }
        //val mutableMenus = menus as MutableList<Menu>
        /* add the default menus if they are not there */
        val fileMenuDef = menus.find { it.text.lowercase() == "&file" }
        if(fileMenuDef == null){
            menus.add(0, Menu(0, "&File"))
        }

        val securityMenuDef = menus.find { it.text.lowercase() == "&security" }
        if(securityMenuDef == null){
            menus.add(menus.size, Menu(0,  "&Security"))
        }

        val developerMenuDef = menus.find { it.text.lowercase() == "&developer" }
        if(developerMenuDef == null){
            menus.add(menus.size, Menu(0, "&Developer"))
        }


        val formsMenuDef = menus.find{ it.text.lowercase() == "fo&rms"}
        if(formsMenuDef == null){
            menus.add(menus.size, Menu(0,  "Fo&rms"))
        }

        val demoMenuDef = menus.find{ it.text.lowercase() == "de&mo"}
        if(demoMenuDef == null){
            menus.add(menus.size, Menu(0,  "De&mo"))
        }

        val systemMenuDef = menus.find{ it.text.lowercase() == "&system"}
        if(systemMenuDef == null){
            menus.add(menus.size, Menu(0,  "&System"))
        }


        val helpMenuDef = menus.find{ it.text.lowercase() == "&help"}
        if(helpMenuDef == null){
            menus.add(menus.size, Menu(0,  "&Help"))
        }

       /********************* actions **********************************/

        /********************* actions **********************************/
        val actionQuit: Action = object : Action("&Quit") {
            override fun run() {
                win.close()
            }
        }
        actionQuit.accelerator = SWT.MOD1 or('Q'.code)
        actionQuit.imageDescriptor = ImageUtils.getImageDescriptor("system-log-out")


        menus.forEach { menuDef ->
            val menu = MenuManager(menuDef.text)

            if(pullFromDatabase){
                val menuItems = MenuItemMapper.getAll(mapOf("menuId" to menuDef.id))
                menuItems.forEach { menuItemDef ->
                    //create an action
                    val action = makeAction(menuItemDef, TabInfo(win.folder, menuItemDef.tabCaption, menuItemDef.font, menuItemDef.image), menuItemScriptRunner)
                    menu.add(action)
                }
            }

            if(menuDef.text.lowercase() == "&file"){
                menu.add(win.actionNew)
                menu.add(win.actionDelete)
                menu.add(win.actionSave)
                menu.add(win.actionRecord)
                menu.add(win.actionPlay)
                menu.add(win.actionStop)
                menu.add(Separator())
                menu.add(actionQuit)
            }

            if(menuDef.text.lowercase() == "&security"){
                makeSecurityActions(menu, win.folder)
            }

            if(menuDef.text.lowercase() == "&developer"){
                makeDeveloperActions(menu, win.folder)
            }

            if(menuDef.text.lowercase() == "&system"){
                makeSystemActions(menu, win.folder)
            }

            if(menuDef.text.lowercase() == "fo&rms"){
                makeFormsActions(menu, win.folder)
            }

            if(menuDef.text.lowercase() == "de&mo"){
                makeDemoActions(menu, win.folder)
            }

            if(menuDef.text.lowercase() == "&help"){
                makeHelpActions(menu, win.folder)
            }

            menuManager.add(menu)
        }
    }
}
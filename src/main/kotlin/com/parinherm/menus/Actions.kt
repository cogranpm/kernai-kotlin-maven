/*
val actionChristmasTree: Action = object: Action("&Christmas Tree") {
override fun run () {
    clearComposite(mainContainer)
    val christmas: ChristmasTreeView = ChristmasTreeView(mainContainer)
    mainContainer.layout()
}
}
actionChristmasTree.accelerator = SWT.MOD1 or ('X'.toInt())
*/

package com.parinherm.menus

import com.parinherm.ApplicationData
import com.parinherm.audio.JLayerAudio
import com.parinherm.entity.MenuItem
import com.parinherm.entity.schema.LoginMapper
import com.parinherm.entity.schema.ViewDefinitionMapper
import com.parinherm.font.FontUtils
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.form.dialogs.ViewDefinitionSelector
import com.parinherm.image.ImageUtils
import com.parinherm.lookups.LookupUtils
import com.parinherm.model.generateClasses
import com.parinherm.script.KotlinScriptRunner
import com.parinherm.server.DefaultViewDefinitions
import com.parinherm.settings.SettingsDialog
import com.parinherm.form.dialogs.AboutDialog
import com.parinherm.view.ChristmasTreeView
import com.parinherm.view.HTMLView
import com.parinherm.view.RecryptView
import com.parinherm.viewmodel.*
import generateDotNetFullStackReact
import org.eclipse.jface.action.Action
import org.eclipse.jface.action.MenuManager
import org.eclipse.jface.window.Window
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.custom.CTabItem
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.FileDialog
import java.io.File

val mindMapperRunner = { tabInfo: TabInfo ->
    val browserTab = CTabItem(tabInfo.folder, SWT.CLOSE)
    browserTab.text = tabInfo.caption
    val image = ImageUtils.getImage(tabInfo.imageKey!!)
    if (image != null) {
        browserTab.image = image
    }
    browserTab.control = ChristmasTreeView(tabInfo.folder)
    tabInfo.folder.selection = browserTab
    JLayerAudio.play()
}

val recryptViewRunner = { tabInfo: TabInfo ->
    val browserTab = CTabItem(tabInfo.folder, SWT.CLOSE)
    browserTab.text = tabInfo.caption
    val image = ImageUtils.getImage(tabInfo.imageKey!!)
    if (image != null) {
        browserTab.image = image
    }
    browserTab.control = RecryptView(tabInfo.folder)
    tabInfo.folder.selection = browserTab
}

val noteBookRunner = { tabInfo: TabInfo ->
    val viewModel = NotebookViewModel(tabInfo)
}

val noteSegmentTypeRunner = { tabInfo: TabInfo ->
    val viewModel = NoteSegmentTypeHeaderViewModel(tabInfo)
}

val shelfRunner = { tabInfo: TabInfo ->
    val viewModel = ShelfViewModel(tabInfo)
}

val personRunner = { tabInfo: TabInfo ->
    val viewModel = PersonViewModel(tabInfo)
}

val recipeRunner = { tabInfo: TabInfo ->
    val viewModel = RecipeViewModel(tabInfo)
}

val snippetsRunner = { tabInfo: TabInfo ->
    val viewModel = SnippetViewModel(tabInfo)
}


val loginRunner = { tabInfo: TabInfo ->
    val viewModel = LoginViewModel(tabInfo)
}

val passwordRunner = { tabInfo: TabInfo ->
    val viewModel = PasswordViewModel(tabInfo, LookupUtils.getLookupIdByKey(LookupUtils.passwordMasterKey))
}

val settingsRunner = { tabInfo: TabInfo ->
    val dialog = SettingsDialog(Display.getCurrent().activeShell);
    if (dialog.open() == Window.OK) {
        if (dialog.reconnect) {
            ApplicationData.reconnect()
        }
    }
}

val salesforceRunner = { tabInfo: TabInfo ->
    val viewModel = SalesforceConfigViewModel(tabInfo)
}

val viewDefRunner = { tabInfo: TabInfo ->
    val viewModel = ViewDefinitionViewModel(tabInfo)
}

val associationDefRunner = { tabInfo: TabInfo ->
    val viewModel = AssociationViewModel(tabInfo)
}

val menusRunner = { tabInfo: TabInfo ->
    val viewModel = MenuViewModel(tabInfo)
}

val lookupsRunner = { tabInfo: TabInfo ->
    val viewModel = LookupViewModel(tabInfo)
}

val scriptRunner = { tabInfo: TabInfo ->
    try {
        //KotlinScriptRunner.runScriptFromView(tabInfo, ViewDefConstants.menuManagerViewId)
        KotlinScriptRunner.runScriptFromView(tabInfo, "transit")
    } catch (e: Exception) {
        ApplicationData.logError(e, null)
    }
}

val generateClassRunner = { tabInfo: TabInfo ->
    try {
        val dialog = ViewDefinitionSelector(Display.getCurrent().activeShell)
        val dialogResult = dialog.open()
        if (dialogResult == Window.OK) {
            if (dialog.selectedEntity != null) {
                generateClasses(listOf(DefaultViewDefinitions.loadView(dialog.selectedEntity!!.viewId)))
            }
        }
    } catch (e: Exception) {
        ApplicationData.logError(e, "Error Generating classes")
    }
}

val generateDotNetFullStackRunner = { tabInfo: TabInfo ->
    try {

        //skip this whilst testing
        val dialog = ViewDefinitionSelector(Display.getCurrent().activeShell)
        val dialogResult = dialog.open()
        if (dialogResult == Window.OK) {
            if(dialog.selectedEntities.isNotEmpty()){
                generateDotNetFullStackReact(dialog.selectedEntities)
            }
        }
        //"companion",
        /*
        val tripRelatedViewDefNames = listOf<String>("airline", "companion", "location", "attraction", "attractionBooking",  "flight", "flightBooking", "flightBookingSeat",
            "frequentFlierAccount", "locationDocument", "lodging", "lodgingStay", "tripDay", "trip")
        val selectedEntities = ViewDefinitionMapper.getAll(mapOf())
        val tripRelatedViewDefs = selectedEntities.filter { viewDefinition -> tripRelatedViewDefNames.contains( viewDefinition.viewId)}
        generateDotNetFullStackReact(tripRelatedViewDefs)
         */
    } catch (e: Exception) {
        ApplicationData.logError(e, "Error Generating classes")
    }
}

val browserTestRunner = { tabInfo: TabInfo ->
    try {
        val browserTab = CTabItem(tabInfo.folder, SWT.CLOSE)
        browserTab.text = tabInfo.caption
        browserTab.control = HTMLView(tabInfo.folder)
        tabInfo.folder.selection = browserTab
    } catch (e: Exception) {
        ApplicationData.logError(e, "Error Generating classes")
    }
}

val aboutDialogRunner = { tabInfo: TabInfo ->
    try {
        val dialog = AboutDialog(Display.getCurrent().activeShell);
        if (dialog.open() == Window.OK) {

        }
    } catch (e: Exception) {
        ApplicationData.logError(e, "Error Generating classes")
    }
}

val exportLoginsRunner = { tabInfo: TabInfo ->
    val dialog = FileDialog(Display.getCurrent().activeShell, SWT.SINGLE or SWT.SAVE)
    val extensions = Array<String>(1){"*.csv"}
    //dialog.filterExtensions(extensions)
    val open = dialog.open()
    println(open)
    val records  = LoginMapper.getAll(mapOf())
    File(open).printWriter().use {
        out ->
        out.println(""""name","username", "password", "url", "notes", "other""")
        records.forEach {
            x ->
                val lookup = LookupUtils.getLookupByKey(LookupUtils.passwordMasterKey, false).find { it.code == x.password}
                if(lookup != null){
                    out.println("\"${x.name}\", \"${x.userName}\", \"${lookup.code}\", \"${x.url}\", \"${x.notes}\", \"${x.other}\"")
                }
        }
    }

}

fun makeAction(
    menuItem: MenuItem,
    tabInfo: TabInfo,
    runner: (tabInfo: TabInfo, menuItem: MenuItem) -> Unit
): Action {
    val action: Action = object : Action(menuItem.text) {
        override fun run() {
            runner(tabInfo, menuItem)
        }
    }
    if (menuItem.acceleratorKey != null && menuItem.acceleratorKey.length > 0) {
        action.accelerator = menuItem.acceleratorKey.codePointAt(0)
    }
    if (menuItem.image != null && menuItem.image.isNotEmpty()) {
        val imageDescriptor = ImageUtils.getImageDescriptor(menuItem.image)
        if (imageDescriptor != null) {
            action.imageDescriptor = imageDescriptor
        }
    }
    return action
}

fun makeAction(
    title: String,
    accelerator: Int?,
    tabInfo: TabInfo,
    runner: (tabInfo: TabInfo) -> Unit
): Action {
    val action: Action = object : Action(title) {
        override fun run() {
            runner(tabInfo)
        }
    }
    if (accelerator != null) {
        action.accelerator = accelerator
    }
    if (tabInfo.imageKey != null) {
        val imageDescriptor = ImageUtils.getImageDescriptor(tabInfo.imageKey)
        if (imageDescriptor != null) {
            action.imageDescriptor = imageDescriptor
        }
    }
    return action
}

fun makeSecurityActions(menu: MenuManager, folder: CTabFolder) {

    val passwordAction = makeAction(
        "Pass&word",
        SWT.MOD1 or ('W'.code),
        TabInfo(folder, "Password", FontUtils.FONT_IBM_PLEX_MONO, ImageUtils.IMAGE_SYSTEM_LOCK_SCREEN),
        passwordRunner
    )

    val loginAction = makeAction(
        "&Login",
        SWT.MOD1 or ('L'.code),
        TabInfo(folder, "Login", FontUtils.FONT_IBM_PLEX_MONO, ImageUtils.IMAGE_APPLICATIONS_INTERNET),
        loginRunner
    )

    val exportAction = makeAction(
        "&Export",
        SWT.MOD1 or ('T'.code),
        TabInfo(folder, "Export", FontUtils.FONT_IBM_PLEX_MONO, ImageUtils.IMAGE_APPLICATIONS_INTERNET),
       exportLoginsRunner
    )

    menu.add(passwordAction)
    menu.add(loginAction)
    menu.add(exportAction)
}

fun makeDeveloperActions(menu: MenuManager, folder: CTabFolder) {
    val notebookAction = makeAction(
        "&Notebooks",
        SWT.MOD1 or ('B'.code),
        TabInfo(folder, "Notebooks", FontUtils.FONT_IBM_PLEX_MONO, ImageUtils.IMAGE_X_OFFICE_DOCUMENT),
        noteBookRunner
    )


    val snippetsAction = makeAction(
        "&Snippets",
        SWT.MOD1 or ('H'.code),
        TabInfo(folder, "Snippets", FontUtils.FONT_IBM_PLEX_MONO, ImageUtils.IMAGE_APPLICATION_X_EXECUTABLE),
        snippetsRunner
    )

    val salesforceAction = makeAction(
        "Salesforce",
        SWT.MOD1 or ('F'.code),
        TabInfo(folder, "Salesforce", FontUtils.FONT_IBM_PLEX_MONO, ImageUtils.IMAGE_SYSTEM_USERS),
        salesforceRunner
    )
    menu.add(snippetsAction)
    menu.add(notebookAction)
    menu.add(salesforceAction);
}


fun makeSystemActions(menu: MenuManager, folder: CTabFolder) {
    val settingsAction = makeAction(
        "&Settings",
        SWT.MOD1 or ('D'.code),
        TabInfo(folder, "Settings", FontUtils.FONT_IBM_PLEX_MONO, ImageUtils.IMAGE_PREFERENCES_SYSTEM),
        settingsRunner
    )

    val viewDefAction = makeAction(
        "&Form Definitions",
        SWT.MOD1 or ('E'.code),
        TabInfo(folder, "Form Definitions", FontUtils.FONT_IBM_PLEX_MONO, ImageUtils.IMAGE_FORMAT_JUSTIFY_FILL),
        viewDefRunner
    )

    val associationDefAction = makeAction(
        "&Association Definitions",
        SWT.MOD1 or ('O'.code),
        TabInfo(folder, "Association Definitions", FontUtils.FONT_IBM_PLEX_MONO, ImageUtils.IMAGE_FORMAT_JUSTIFY_FILL),
        associationDefRunner
    )


    val menusAction = makeAction(
        "&Menus",
        SWT.MOD1 or ('M'.code),
        TabInfo(folder, "Menus", FontUtils.FONT_IBM_PLEX_MONO, ImageUtils.IMAGE_PREFERENCES_DESKTOP),
        menusRunner
    )

    val lookupsAction = makeAction(
        "&Lookups",
        SWT.MOD1 or ('U'.code),
        TabInfo(folder, "Lookups", FontUtils.FONT_IBM_PLEX_MONO, ImageUtils.IMAGE_EDIT_FIND),
        lookupsRunner
    )

    val noteSegmentTypeAction = makeAction(
        "&Note Segment Types",
        SWT.MOD1 or ('T'.code),
        TabInfo(folder, "Note Segment Type", FontUtils.FONT_IBM_PLEX_MONO, ImageUtils.IMAGE_PREFERENCES_DESKTOP_FONT),
        noteSegmentTypeRunner
    )


    val recryptAction = makeAction(
        "&Encrypt Data",
        SWT.MOD1 or ('Y'.code),
        TabInfo(folder, "Encrypt Data", FontUtils.FONT_IBM_PLEX_MONO, ImageUtils.IMAGE_SYSTEM_INSTALLER),
        recryptViewRunner
    )

    menu.add(settingsAction)
    menu.add(viewDefAction)
    menu.add(associationDefAction)
    menu.add(menusAction)
    menu.add(lookupsAction)
    menu.add(noteSegmentTypeAction)
    //menu.add(recryptAction)
}

fun makeFormsActions(menu: MenuManager, folder: CTabFolder) {
    val recipeAction = makeAction(
        "&Recipe",
        SWT.MOD1 or ('R'.code),
        TabInfo(folder, "Recipes", FontUtils.FONT_IBM_PLEX_MONO, ImageUtils.IMAGE_EDIT_PASTE),
        recipeRunner
    )


    val shelfAction = makeAction(
        "&Bookshelf",
        SWT.MOD1 or ('F'.code),
        TabInfo(folder, "Bookshelf", FontUtils.FONT_IBM_PLEX_MONO, ImageUtils.IMAGE_MICROPHONE),
        shelfRunner
    )

    menu.add(recipeAction)
    menu.add(shelfAction)
}

fun makeHelpActions(menu: MenuManager, folder: CTabFolder) {

    val aboutAction = makeAction(
        "&About",
        null,
        TabInfo(folder, "About", FontUtils.FONT_IBM_PLEX_MONO, null),
        aboutDialogRunner
    )
    menu.add(aboutAction)
}

fun makeDemoActions(menu: MenuManager, folder: CTabFolder) {

    val mindMapperAction = makeAction(
        "&Mind Mapper",
        SWT.MOD1 or ('M'.code),
        TabInfo(folder, "Mind Mapper", FontUtils.FONT_IBM_PLEX_MONO, ImageUtils.IMAGE_DIALOG_INFORMATION),
        mindMapperRunner
    )


    val personAction = makeAction(
        "&People",
        SWT.MOD1 or ('L'.code),
        TabInfo(folder, "People", FontUtils.FONT_IBM_PLEX_MONO, ImageUtils.IMAGE_CONTACT_NEW),
        personRunner
    )

    val scriptRunAction = makeAction(
        "&Run Script",
        SWT.MOD1 or ('T'.code),
        TabInfo(folder, "Run Script", FontUtils.FONT_IBM_PLEX_MONO, ImageUtils.IMAGE_APPLICATION_X_EXECUTABLE),
        scriptRunner
    )

    val generateClassAction = makeAction(
        "&Generate Classes",
        SWT.MOD1 or ('L'.code),
        TabInfo(folder, "Generate Classes", FontUtils.FONT_IBM_PLEX_MONO, ImageUtils.IMAGE_APPLICATIONS_SYSTEM),
        generateClassRunner
    )

    val generateDotNetFullStackAction = makeAction(
        "&Generate DotNet Full Stack",
        SWT.MOD1 or ('K'.code),
        TabInfo(folder, "Generate DotNet Full Stack", FontUtils.FONT_IBM_PLEX_MONO, ImageUtils.IMAGE_APPLICATIONS_SYSTEM),
        generateDotNetFullStackRunner
    )


    val browserTestAction = makeAction(
        "Browser &Test",
        null,
        TabInfo(folder, "Browser Test", FontUtils.FONT_IBM_PLEX_MONO, ImageUtils.IMAGE_INTERNET_WEB_BROWSER),
        browserTestRunner
    )

    menu.add(personAction)
    menu.add(mindMapperAction)
    menu.add(browserTestAction)
    menu.add(scriptRunAction)
    menu.add(generateClassAction)
    menu.add(generateDotNetFullStackAction);

}


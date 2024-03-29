package com.parinherm


import com.parinherm.viewmodel.*
import org.eclipse.jface.action.*
import org.eclipse.jface.window.ApplicationWindow
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.custom.CTabItem
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.*
import java.io.IOException


/*
inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)
fun getViewDefinitions(): Map<String, Any>{
   val views = Gson().fromJson<Map<String, Any>>( HttpClient.getViews())
    return views
}
 */


class MainWindow(parentShell: Shell?): ApplicationWindow(parentShell) {

    lateinit var mainContainer: Composite
    lateinit var folder: CTabFolder

    val actionSave: Action = object : Action("&Save") {
        override fun run() {
            val selection = folder.selection
            if(selection != null) {
                val tabData = ApplicationData.tabs[selection.getData("key")]
                if (tabData != null) {
                    if(!tabData.isClosed) {
                        val viewModel = tabData.viewModel
                        viewModel.save()
                    }
                }
            }
        }
    }


    val actionNew: Action = object : Action("&New") {
        override fun run() {
            val selection = folder.selection
            if(selection != null) {
                val tabData = ApplicationData.tabs[selection.getData("key")]
                if (tabData != null) {
                    if(!tabData.isClosed) {
                        val viewModel = tabData.viewModel
                        viewModel.new()
                    }
                }
            }
        }
    }

    val actionDelete: Action = object: Action("&Delete") {
        override fun run() {
            val selection = folder.selection
            if(selection != null) {
                val tabData = ApplicationData.tabs[selection.getData("key")]
                if (tabData != null) {
                    if(!tabData.isClosed) {
                        val viewModel = tabData.viewModel
                        viewModel.delete()
                    }
                }
            }
        }
    }

    init {
        actionSave.accelerator = SWT.MOD1 or('S'.toInt())
        actionNew.accelerator = SWT.MOD1 or('N'.toInt())
        //actionDelete.accelerator = SWT.DEL
        addToolBar(SWT.NONE) //SWT.FLAT or SWT.WRAP
        addMenuBar()
        addStatusLine()
        actionSave.isEnabled = false
        actionNew.isEnabled = true
        actionDelete.isEnabled = false
    }

    override fun createContents(parent: Composite?): Control {
        mainContainer = Composite(parent, SWT.NONE)
        mainContainer.layout = FillLayout()
        folder = CTabFolder(mainContainer, SWT.TOP or SWT.BORDER)
        folder.addDisposeListener { println("I am disposed")}


        // is at least 1 tab required?
        val homeTab = CTabItem(folder, SWT.NONE)
        homeTab.text = "Home"
        folder.selection = homeTab



        mainContainer.layout()
        return mainContainer
    }

    override fun createMenuManager(): MenuManager {
        val win = this

       val actionOpenFile: Action = object : Action("Open") {
            override fun run() {
                val dialog = FileDialog(shell, SWT.OPEN)
                val file = dialog.open()
                if (file != null) {
                    try {
                        setStatus("File loaded successfully")
                    } catch (e: IOException) {
                        e.printStackTrace()
                        setStatus("Failed to load file")
                    }

                }
            }
        }

        val actionQuit: Action = object : Action("&Quit") {
            override fun run() {
                win.close()
            }
        }
        actionQuit.accelerator = SWT.MOD1 or('Q'.toInt())



        val actionChristmasTree: Action = object: Action("&Christmas Tree") {
            override fun run () {
                clearComposite(mainContainer)
                val christmas: ChristmasTreeView = ChristmasTreeView(mainContainer)
                mainContainer.layout()
            }
        }
        actionChristmasTree.accelerator = SWT.MOD1 or ('X'.toInt())

        val personAction: Action = object: Action("&People") {
            override fun run () {
                val viewModel = PersonViewModel(folder)
                ApplicationData.makeTab(viewModel, "People", ApplicationData.TAB_KEY_PERSON)
            }
        }
        personAction.accelerator = SWT.MOD1 or ('P'.toInt())

        val recipeAction: Action = object: Action("&Recipes") {
            override fun run () {
                val viewModel = RecipeViewModel(folder)
                ApplicationData.makeTab(viewModel, "Recipes", ApplicationData.TAB_KEY_RECIPE)
            }
        }
        recipeAction.accelerator = SWT.MOD1 or ('R'.toInt())

        val snippetsAction: Action = object: Action("&Snippets") {
            override fun run () {
                val viewModel = SnippetViewModel(folder)
                ApplicationData.makeTab(viewModel, "Snippets", ApplicationData.TAB_KEY_SNIPPET)
            }
        }
        snippetsAction.accelerator = SWT.MOD1 or ('E'.toInt())

        val loginAction: Action = object: Action("&Logins") {
            override fun run () {
                val viewModel = LoginViewModel(folder)
                ApplicationData.makeTab(viewModel, "Logins", ApplicationData.TAB_KEY_LOGINS)
            }
        }
        loginAction.accelerator = SWT.MOD1 or ('L'.toInt())

        val notebookAction: Action = object: Action("&Notebooks") {
            override fun run () {
                val viewModel = NotebookViewModel(folder)
                ApplicationData.makeTab(viewModel, "Notebooks", ApplicationData.TAB_KEY_NOTEBOOK)
            }
        }
        notebookAction.accelerator = SWT.MOD1 or ('B'.toInt())

        val lookupsAction: Action = object: Action("&Lookup") {
            override fun run () {
                val viewModel = LookupViewModel(folder)
                ApplicationData.makeTab(viewModel, "Lookup", ApplicationData.TAB_KEY_LOOKUP)
            }
        }
        lookupsAction.accelerator = SWT.MOD1 or ('L'.toInt())

        val shelfAction: Action = object: Action("&Shelf") {
            override fun run () {
                val viewModel = ShelfViewModel(folder)
                ApplicationData.makeTab(viewModel, "Shelf", ApplicationData.TAB_KEY_SHELF)
            }
        }
        shelfAction.accelerator = SWT.MOD1 or ('F'.toInt())

        val noteSegmentTypeAction: Action = object: Action("&Note Segments") {
            override fun run () {
                val viewModel = NoteSegmentTypeHeaderViewModel(folder)
                ApplicationData.makeTab(viewModel, "Note Segment Type", ApplicationData.TAB_KEY_NOTESEGMENTTYPEHEADER)
            }
        }
        noteSegmentTypeAction.accelerator = SWT.MOD1 or ('T'.toInt())


        val menuManager = MenuManager("")
        val fileMenu = MenuManager("&File")
        val actionMenu = MenuManager("&Action")

        // file menus
        fileMenu.add(Separator())
        fileMenu.add(actionOpenFile)
        fileMenu.add(actionNew)
        fileMenu.add(actionDelete)
        fileMenu.add(actionSave)
        fileMenu.add(actionQuit)

        // action menus
        actionMenu.add(actionChristmasTree)
        actionMenu.add(personAction)
        actionMenu.add(recipeAction)
        actionMenu.add(snippetsAction)
        actionMenu.add(loginAction)
        actionMenu.add(notebookAction)
        actionMenu.add(lookupsAction)
        actionMenu.add(shelfAction)
        actionMenu.add(noteSegmentTypeAction)

        menuManager.add(fileMenu)
        menuManager.add(actionMenu)
        menuManager.updateAll(true)
        return menuManager
    }

    override fun createToolBarManager(style: Int): ToolBarManager {
        val toolBarManager = ToolBarManager(style);
        val save = ActionContributionItem(actionSave)
        val new = ActionContributionItem(actionNew)
        val delete = ActionContributionItem(actionDelete)
        toolBarManager.add(save)
        toolBarManager.add(new)
        toolBarManager.add(delete)
        toolBarManager.update(false)
        return toolBarManager
    }



    override fun createStatusLineManager(): StatusLineManager {
        return StatusLineManager()
    }


    override fun configureShell(shell: Shell?) {
        super.configureShell(shell)
        shell?.maximized = true
        shell?.text = "Kernai Kotlin"

        ApplicationData.setupImages()
        val activitySmall: Image = ApplicationData.getImage(ApplicationData.IMAGE_ACTVITY_SMALL)
        val activityLarge: Image = ApplicationData.getImage(ApplicationData.IMAGE_ACTIVITY_LARGE)
        val images = arrayOf<Image>(activitySmall, activityLarge)
        shell?.images = images
    }



    override fun getInitialSize(): Point {
        return  getShellSize()
    }

    private fun getShellSize(): Point {
        val width = Display.getDefault().primaryMonitor.clientArea.width
        val height = Display.getDefault().primaryMonitor.clientArea.width
        return Point(width, height)
    }
}
package com.parinherm


import com.parinherm.font.FontUtils
import com.parinherm.image.ImageUtils
import com.parinherm.integration.SalesforceRest
import com.parinherm.menus.MenuUtils
import com.parinherm.settings.Setting
import com.parinherm.view.HomeView
import com.parinherm.viewmodel.*
import kotlinx.coroutines.*
import org.eclipse.jface.action.*
import org.eclipse.jface.window.ApplicationWindow
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.custom.CTabItem
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.events.ShellAdapter
import org.eclipse.swt.events.ShellEvent
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.*
import java.lang.Exception

class MainWindow(parentShell: Shell?) : ApplicationWindow(parentShell) {

    lateinit var mainContainer: Composite
    lateinit var folder: CTabFolder
    val statusLine = StatusLineManager()

    val actionSave: Action = object : Action("&Save") {
        override fun run() {
            val selection = folder.selection
            if (selection != null) {
                val tabData = ApplicationData.tabs[selection.getData("key")]
                if (tabData != null) {
                    if (!tabData.isClosed) {
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
            if (selection != null) {
                val tabData = ApplicationData.tabs[selection.getData("key")]
                if (tabData != null) {
                    if (!tabData.isClosed) {
                        val viewModel = tabData.viewModel
                        viewModel.new()
                    }
                }
            }
        }
    }

    val actionDelete: Action = object : Action("&Delete") {
        override fun run() {
            val selection = folder.selection
            if (selection != null) {
                val tabData = ApplicationData.tabs[selection.getData("key")]
                if (tabData != null) {
                    if (!tabData.isClosed) {
                        val viewModel = tabData.viewModel
                        viewModel.delete()
                    }
                }
            }
        }
    }

    val actionRecord: Action = object : Action("&Record") {
        override fun run() {
            val selection = folder.selection
            if (selection != null) {
                val tabData = ApplicationData.tabs[selection.getData("key")]
                if (tabData != null) {
                    if (!tabData.isClosed) {
                        val viewModel = tabData.viewModel
                        viewModel.record()
                    }
                }
            }
        }
    }

    val actionPlay: Action = object : Action("&Play") {
        override fun run() {
            val selection = folder.selection
            if (selection != null) {
                val tabData = ApplicationData.tabs[selection.getData("key")]
                if (tabData != null) {
                    if (!tabData.isClosed) {
                        val viewModel = tabData.viewModel
                        viewModel.play()
                    }
                }
            }
        }
    }

    val actionStop: Action = object : Action("&Stop") {
        override fun run() {
            val selection = folder.selection
            if (selection != null) {
                val tabData = ApplicationData.tabs[selection.getData("key")]
                if (tabData != null) {
                    if (!tabData.isClosed) {
                        val viewModel = tabData.viewModel
                        viewModel.stop()
                    }
                }
            }
        }
    }


    init {
        actionSave.accelerator = SWT.MOD1 or ('S'.code)
        actionNew.accelerator = SWT.MOD1 or ('N'.code)
        actionRecord.accelerator = SWT.MOD1.or(SWT.SHIFT).or('r'.code)
        actionStop.accelerator = SWT.MOD1.or(SWT.SHIFT).or('s'.code)
        actionPlay.accelerator = SWT.MOD1.or(SWT.SHIFT).or('p'.code)
        //actionDelete.accelerator = SWT.DEL
        addToolBar(SWT.NONE) //SWT.FLAT or SWT.WRAP
        addMenuBar()
        addStatusLine()

        //got a problem with dirty handling
        //actionSave.isEnabled = false
        actionSave.isEnabled = true
        actionNew.isEnabled = true
        actionDelete.isEnabled = false
        actionRecord.isEnabled = true
        actionStop.isEnabled = true
        actionPlay.isEnabled = true
    }

    override fun createContents(parent: Composite?): Control {
        mainContainer = Composite(parent, SWT.NONE)
        mainContainer.layout = FillLayout()
        folder = CTabFolder(mainContainer, SWT.TOP or SWT.BORDER)
        //folder.addDisposeListener { println("I am disposed")}
        folder.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                super.widgetSelected(e)
                val selection = folder.selection
                if (selection != null) {
                    val tabData = ApplicationData.tabs[selection.getData("key")]
                    if (tabData != null) {
                        if (!tabData.isClosed) {
                            val viewModel = tabData.viewModel
                            viewModel.activated()
                        }
                    }
                }
            }
        })

        // is at least 1 tab required?
        val homeTab = CTabItem(folder, SWT.NONE)
        homeTab.text = "Home"
        homeTab.font = FontUtils.getFont(FontUtils.FONT_IBM_PLEX_MONO)
        homeTab.image = ImageUtils.getImage("go-home")
        homeTab.control = HomeView(folder)
        folder.selection = homeTab
        mainContainer.layout()

        actionSave.imageDescriptor = ImageUtils.getImageDescriptor("document-save")
        actionNew.imageDescriptor = ImageUtils.getImageDescriptor("document-new")
        actionDelete.imageDescriptor = ImageUtils.getImageDescriptor("user-trash")
        actionRecord.imageDescriptor = ImageUtils.getImageDescriptor("media-record")
        actionPlay.imageDescriptor = ImageUtils.getImageDescriptor("media-playback-start")
        actionStop.imageDescriptor = ImageUtils.getImageDescriptor("media-playback-stop")

        MenuUtils.makeMenus(this, this.menuBarManager, false)
        this.menuBarManager.updateAll(true)
        var setting: Setting = Setting("", "", "", "", "", "")
        ApplicationData.startupRoutine(Display.getDefault(), setting, false)
        val job = CoroutineScope(Dispatchers.Default).launch {
            try {
                ApplicationData.startupTasks()
                refreshMenus()
            } catch (ex: Exception) {
                ApplicationData.logError(ex, "Error in startup tasks")
            }
        }
        /*
        Display.getDefault().asyncExec {
            try {
                var setting: Setting = Setting("", "", "", "", "", "")
                ApplicationData.startupRoutine(Display.getDefault(), setting, false)
                val job = CoroutineScope(Dispatchers.Default).launch {
                    try {
                        ApplicationData.startupTasks()
                    } catch (ex: Exception) {
                        println(ex.message)
                    }
                }
                MenuUtils.makeMenus(ApplicationData.mainWindow, ApplicationData.mainWindow.menuBarManager, true)
            } catch (e: Exception) {

            } finally {
            }
        }
         */
        return mainContainer
    }


    override fun createMenuManager(): MenuManager {
        val menuManager = MenuManager("")
        menuManager.updateAll(true)
        return menuManager
    }

    fun refreshMenus() {
        shell.display.asyncExec {
            menuBarManager.removeAll()
            MenuUtils.makeMenus(this, menuBarManager, true)
            menuBarManager.updateAll(true)
        }
    }

    override fun createToolBarManager(style: Int): ToolBarManager {
        val toolBarManager = ToolBarManager(style);
        val save = ActionContributionItem(actionSave)
        val new = ActionContributionItem(actionNew)
        val delete = ActionContributionItem(actionDelete)
        val record = ActionContributionItem(actionRecord)
        val stop = ActionContributionItem(actionStop)
        val play = ActionContributionItem(actionPlay)
        toolBarManager.add(save)
        toolBarManager.add(new)
        toolBarManager.add(delete)
        toolBarManager.add(record)
        toolBarManager.add(stop)
        toolBarManager.add(play)
        toolBarManager.update(false)
        return toolBarManager
    }


    override fun createStatusLineManager(): StatusLineManager {
        return statusLine
    }

    override fun create() {
        super.create()
    }

    override fun close(): Boolean {
        try {
            ApplicationData.close()
            return super.close()
        } catch(e: Exception){
            ApplicationData.logError(e, "Error on MainWindow close")
            return false
        }
    }

    override fun configureShell(shell: Shell?) {
        super.configureShell(shell)
        shell?.font = FontUtils.getFont(FontUtils.FONT_IBM_PLEX_MONO)
        shell?.text = "${ApplicationData.APPLICATION_NAME}"
        /*
        val activitySmall: Image = ImageUtils.getImage(ImageUtils.IMAGE_ACTVITY_SMALL)
        val activityLarge: Image = ImageUtils.getImage(ImageUtils.IMAGE_ACTIVITY_LARGE)
        val images = arrayOf<Image>(activitySmall, activityLarge)
         */
        val logoSmall = ImageUtils.getImage(ImageUtils.IMAGE_LOGO_SMALL)
        val logo = ImageUtils.getImage(ImageUtils.IMAGE_LOGO)
        if (logo != null && logoSmall != null) {
            shell?.images = arrayOf<Image>(logoSmall, logo)
        }
        shell?.minimumSize = Point(640, 480)
        shell?.maximized = true

        shell?.addShellListener(object : ShellAdapter() {
            override fun shellActivated(e: ShellEvent?) {
                super.shellActivated(e)
            }
        })
        shell?.addShellListener(object : ShellAdapter() {
            override fun shellClosed(e: ShellEvent?) {
                super.shellClosed(e)
            }
        })

        shell?.addListener(SWT.OPEN, object : Listener {
            override fun handleEvent(p0: Event?) {
            }
        })
    }

    override fun getInitialSize(): Point {
        return getShellSize()
    }

    private fun getShellSize(): Point {
        val width = Display.getDefault().primaryMonitor.clientArea.width
        val height = Display.getDefault().primaryMonitor.clientArea.width
        return Point(width, height)
    }
}
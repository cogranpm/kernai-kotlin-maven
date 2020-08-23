package com.parinherm

//import org.eclipse.nebula.widgets.pshelf.*


import com.parinherm.builders.swtBuilder
import com.parinherm.databinding.DataBindingView
import com.parinherm.tests.TestData
import org.eclipse.jface.action.*
import org.eclipse.jface.window.ApplicationWindow
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.custom.CTabItem
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.*
import java.io.IOException


class MainWindow(parentShell: Shell?): ApplicationWindow(parentShell) {

    lateinit var mainContainer: Composite


    val actionSave: Action = object : Action("&Save") {
        override fun run() {
           println("Saving")
        }
    }



    init {
        actionSave.accelerator = SWT.MOD1 or('S'.toInt())
        addMenuBar()
        addToolBar(SWT.WRAP)
        addStatusLine()

    }


    override fun createContents(parent: Composite?): Control {
        val container = Composite(parent, SWT.NONE)
        container.layout = FillLayout()
        val folder = CTabFolder(container, SWT.TOP or SWT.BORDER)
        val item = CTabItem(folder, SWT.NONE)
        item.text = "&Getting Started"

        /* testing a load ui definitions from server
        scenario, so a api site would be running
        ui defs are put on wire format and downloaded
        and then renderer takes care of constructing the widgets etc
         */
        //swtBuilder.renderTest()
        val view = swtBuilder.renderView(TestData.data, folder, ApplicationData.ViewDef.bindingTestViewId)
        item.control = view
        //DataBindingView(TestData.data).makeView(folder)


        return container
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

        val actionDataBinding: Action = object: Action("&Data Binding") {
            override fun run () {
                println("here")
                clearComposite(mainContainer)
                val view: Composite = DataBindingView(TestData.data).makeView(mainContainer)
                mainContainer.layout()
            }
        }
        actionDataBinding.accelerator = SWT.MOD1 or ('B'.toInt())

        val menuManager = MenuManager("")
        val fileMenu = MenuManager("&File")
        val actionMenu = MenuManager("&Action")

        // file menus
        fileMenu.add(Separator())
        fileMenu.add(actionOpenFile)
        fileMenu.add(actionSave)
        fileMenu.add(actionQuit)

        // action menus
        actionMenu.add(actionChristmasTree)
        actionMenu.add(actionDataBinding)

        menuManager.add(fileMenu)
        menuManager.add(actionMenu)
        return menuManager
    }

    override fun createToolBarManager(style: Int): ToolBarManager {
        val toolBarManager = ToolBarManager(SWT.NONE);
        toolBarManager.update(true)
        val save = ActionContributionItem(actionSave)
        toolBarManager.add(save)
        return toolBarManager
    }



    override fun createStatusLineManager(): StatusLineManager {
        return StatusLineManager()
    }


    override fun configureShell(shell: Shell?) {
        super.configureShell(shell)
        shell?.text = "Kernai Kotlin"

        ApplicationData.setupImages()
        val activitySmall: Image = ApplicationData.getImage(ApplicationData.IMAGE_ACTVITY_SMALL)
        val activityLarge: Image = ApplicationData.getImage(ApplicationData.IMAGE_ACTIVITY_LARGE)
        val images = arrayOf<Image>(activitySmall, activityLarge)
        shell?.images = images
    }



    override fun getInitialSize(): Point {
        return Point(900, 900)
    }
}
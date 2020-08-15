package com.parinherm

import org.eclipse.jface.action.*
import org.eclipse.jface.resource.ImageDescriptor
import org.eclipse.jface.window.ApplicationWindow
import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Point
import org.eclipse.nebula.widgets.pshelf.*
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.layout.FillLayout
import java.io.IOException
import java.io.File
import org.eclipse.jface.resource.ImageDescriptor.createFromFile
import org.eclipse.swt.widgets.*
import org.eclipse.swt.graphics.Image



import com.parinherm.databinding.DataBindingView

class MainWindow (parentShell: Shell?): ApplicationWindow(parentShell) {

    lateinit var mainContainer: Composite

    init {
        addMenuBar()
        addToolBar(SWT.WRAP)
        addStatusLine()
    }


    override fun createContents(parent: Composite?): Control {
        val container = Composite(parent, SWT.NONE)
        container.layout = FillLayout()
        val sashForm: SashForm = SashForm(container, SWT.HORIZONTAL or (SWT.BORDER))
        sashForm.sashWidth = 3
        val weights: Array<Int> = arrayOf(1, 3)
        val navContainer: Composite = Composite(sashForm, SWT.NONE)
        mainContainer = Composite(sashForm, SWT.NONE)
        sashForm.weights = weights.toIntArray()
        navContainer.layout= FillLayout(SWT.VERTICAL)
        mainContainer.layout = FillLayout(SWT.VERTICAL)

        val lblName: Label = getLabel("Navigation Item", navContainer)
        val lblAddress: Composite = DataBindingView.makeView(mainContainer)


        return container
    }

    override fun createMenuManager(): MenuManager {
        val win = this

       val actionOpenFile: Action = object : Action("Open") {
            override fun run() {
                val dialog = FileDialog(shell, SWT.OPEN )
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

        val actionChristmasTree: Action = object: Action ("&Christmas Tree") {
            override fun run () {
                clearComposite(mainContainer)
                val christmas: ChristmasTreeView = ChristmasTreeView(mainContainer)
                mainContainer.layout()
            }
        }
        actionChristmasTree.accelerator = SWT.MOD1 or ('X'.toInt())

        val actionDataBinding: Action = object: Action ("&Data Binding") {
            override fun run () {
                println("here")
                clearComposite(mainContainer)
                val view: Composite = DataBindingView.makeView(mainContainer)
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
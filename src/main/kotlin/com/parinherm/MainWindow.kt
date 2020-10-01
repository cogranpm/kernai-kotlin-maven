package com.parinherm

//import org.eclipse.nebula.widgets.pshelf.*


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.parinherm.builders.*
import com.parinherm.databinding.DataBindingView
import com.parinherm.entity.BeanTest
import com.parinherm.entity.schema.BeanTestMapper
import com.parinherm.tests.BeansBindingTestData
import com.parinherm.tests.TestData
import com.parinherm.viewmodel.BeanTestViewModel
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


inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)

fun getViewDefinitions(): Map<String, Any>{
    val views = Gson().fromJson<Map<String, Any>>( HttpClient.getViews())
    return views
}


class MainWindow(parentShell: Shell?): ApplicationWindow(parentShell) {

    lateinit var mainContainer: Composite
    lateinit var folder: CTabFolder

    val actionSave: Action = object : Action("&Save") {
        override fun run() {
           println("Saving")
        }
    }

    init {
        actionSave.accelerator = SWT.MOD1 or('S'.toInt())
        addToolBar(SWT.NONE) //SWT.FLAT or SWT.WRAP
        addMenuBar()
        addStatusLine()

    }

    override fun createContents(parent: Composite?): Control {
        mainContainer = Composite(parent, SWT.NONE)
        mainContainer.layout = FillLayout()
        folder = CTabFolder(mainContainer, SWT.TOP or SWT.BORDER)


        val data = BeanTestMapper.getAll(mapOf()) //BeansBindingTestData.data
        val viewModel = BeanTestViewModel(data, BeanTest.Comparator(), ModelBinder<BeanTest>())
        ApplicationData.makeTab(viewModel, "Data binding Test", ApplicationData.TAB_KEY_DATA_BINDING_TEST, ApplicationData.ViewDef.beansBindingTestViewId)

        /* this messes up the layout here or
        in the toolbar manager override

        val save = ActionContributionItem(actionSave)
        toolBarManager.add(save)
        toolBarManager.update(false)

         */

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
        menuManager.updateAll(true)
        return menuManager
    }

    override fun createToolBarManager(style: Int): ToolBarManager {
        val toolBarManager = ToolBarManager(style);
        val save = ActionContributionItem(actionSave)
        toolBarManager.add(save)
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
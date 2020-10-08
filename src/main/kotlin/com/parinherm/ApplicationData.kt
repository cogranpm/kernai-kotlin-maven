package com.parinherm

import com.parinherm.entity.LookupDetail
import com.parinherm.entity.schema.SchemaBuilder
import com.parinherm.form.IFormViewModel
import com.parinherm.server.SimpleHttpServer
import com.parinherm.server.ViewDefinitions
import org.eclipse.core.databinding.UpdateValueStrategy
import org.eclipse.core.databinding.observable.Realm
import org.eclipse.jface.databinding.swt.DisplayRealm
import org.eclipse.jface.resource.ImageDescriptor
import org.eclipse.jface.resource.ImageRegistry
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabItem
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.widgets.Display

object ApplicationData {

    private lateinit var imageRegistry: ImageRegistry
    private lateinit var mainWindow: MainWindow
    lateinit var viewDefinitions: Map<String, Any>
    /* keeps all opened tabs in a list and remove them when closed */
    var tabs: MutableMap<String, TabInstance> = mutableMapOf()

    const val  IMAGE_ACTVITY_SMALL = "activitysmall"
    const val IMAGE_ACTIVITY_LARGE = "activitylarge"
    const val  IMAGE_STOCK_INFO = "stock_info"
    const val IMAGE_STOCK_EXIT = "stock_exit"
    const val  IMAGE_GOUP = "goup"
    const val IMAGES_PATH = "/images/"

    const val TAB_KEY_PERSON = "person"
    const val TAB_KEY_PERSONDETAIL = "persondetail"


    const val swnone = SWT.NONE
    const val labelStyle = SWT.BORDER
    const val listViewStyle = SWT.SINGLE or SWT.H_SCROLL or SWT.V_SCROLL or SWT.FULL_SELECTION or SWT.BORDER

    const val serverPort = "8080"
    const val serverHost = "localhost"
    const val serverProtocol = "http"
    val urls = mapOf<String, String>("views" to "views")

    val views = ViewDefinitions.makeDefinitions()
    const val countryLookupKey = "country"
    const val speciesLookupKey = "species"

    val defaultUpdatePolicy = UpdateValueStrategy.POLICY_UPDATE  //UpdateValueStrategy.POLICY_ON_REQUEST

    init {

    }

    fun start() : Unit {
        SimpleHttpServer.start()

        SchemaBuilder.build()

        val display: Display = Display.getDefault()
        Realm.runWithDefault(DisplayRealm.getRealm(display)) {
            try {
                viewDefinitions = getViewDefinitions()
                imageRegistry = ImageRegistry()
                mainWindow = MainWindow(null)
                mainWindow.setBlockOnOpen(true)
                mainWindow.open()
                Display.getCurrent().dispose()
            } catch (ex: Exception){
                println (ex.message)
            }
        }
        SimpleHttpServer.stop()
    }


    /************************** new way ***************************************************/
    fun makeTab(viewModel: IFormViewModel, caption: String, key: String) : Unit {
        if (tabs.containsKey(key) && tabs[key] != null ) {
            if (tabs[key]!!.isClosed){
                // set it to open and create the tab
                tabs[key] = createTab(viewModel, caption, key)
            } else {
                // set focus to existing tab somehow

            }
        } else {
            tabs[key] = createTab(viewModel, caption, key)
        }
    }

    private fun createTab(viewModel: IFormViewModel, caption: String, key:String): TabInstance {
        val tabItem = CTabItem(mainWindow.folder, SWT.CLOSE)
        tabItem.text = caption
        tabItem.control = viewModel.render()
        tabItem.addDisposeListener {
            tabs[key]!!.isClosed = true
        }
        tabItem.setData("key", key)
        mainWindow.folder.selection = tabItem
        return TabInstance(viewModel, tabItem, false)
    }


    /************************ old way **************************************************
    fun makeTab(viewModel: ViewModel<*>, caption: String, key: String, viewId: String) : Unit {
        if (tabs.containsKey(key) && tabs[key] != null ) {
            if (tabs[key]!!.isClosed){
                // set it to open and create the tab
                tabs[key] = createTab(viewModel, caption, key, viewId)
            } else {
                // set focus to existing tab somehow

            }
        } else {
           tabs[key] = createTab(viewModel, caption, key, viewId)
        }
    }

    private fun createTab(viewModel: ViewModel<*>, caption: String, key:String, viewId: String): TabInstance {
        val formDef: Map<String, Any> = getView(viewId, viewDefinitions)
        val tabItem = CTabItem(mainWindow.folder, SWT.CLOSE)
        tabItem.text = caption
        tabItem.control = viewModel.render(mainWindow.folder, formDef)
        tabItem.addDisposeListener {
            tabs[key]!!.isClosed = true
        }
        tabItem.setData("key", key)
        mainWindow.folder.selection = tabItem
        return TabInstance(viewModel, tabItem, false)
    }
    */


    fun getView(viewId: String): Map<String, Any> {
        val forms: List<Map<String, Any>> = views[ViewDef.forms] as List<Map<String, Any>>
        return forms.first { it[ViewDef.viewid] == viewId }
    }

    fun getView(viewId: String, viewDefinitions: Map<String, Any>): Map<String, Any>  {
        val forms: List<Map<String, Any>> = viewDefinitions[ViewDef.forms] as List<Map<String, Any>>
        return forms.first { it[ViewDef.viewid] == viewId }
    }

    fun makeServerUrl(urlKey: String) : String = "$serverProtocol://$serverHost:$serverPort/${urls[urlKey]}"

    fun setupImages() {
        try {
            putImage(IMAGE_ACTVITY_SMALL, "Activity_16xSM.png")
            putImage(IMAGE_ACTIVITY_LARGE, "Activity_32x.png")
            putImage(IMAGE_GOUP, "go-up.png")
            putImage(IMAGE_STOCK_EXIT, "stock_exit_24.png")
            putImage(IMAGE_STOCK_INFO, "stock_save_24.png")
        }
        catch(e: Exception) {
            println(e)
        }
    }

    private fun putImage(key: String, filename: String) = try {
        val path: String = IMAGES_PATH + filename
        this.imageRegistry.put(key, ImageDescriptor.createFromFile(ApplicationData.javaClass, path))
    } catch ( e: Exception) {
        println(e)
    }

    public fun getImage(name: String): Image {
        return this.imageRegistry.get(name)
    }

    val countryList: List<LookupDetail> = listOf(
        LookupDetail("Aus", "Australia"),
        LookupDetail("Can", "Canada"),
        LookupDetail("Bra", "Brazil"),
        LookupDetail("SA", "South Africa")
    )

    val speciesList: List<LookupDetail> = listOf(
            LookupDetail("L", "Lizard"),
            LookupDetail("C", "Cat"),
            LookupDetail("D", "Dog"),
            LookupDetail("E", "Elephant"),
            LookupDetail("M", "Mongoose"),
            LookupDetail("R", "Rabbit"),
            LookupDetail("F", "Frog"),
            LookupDetail("J", "Jackle")
    )

    val lookups: Map<String, List<LookupDetail>> = mapOf(
            countryLookupKey to countryList,
            speciesLookupKey to speciesList
    )

    public object ViewDef{

        const val bindingTestViewId = "bindingtest"
        const val beansBindingTestViewId = "beansbinding"
        const val personDetailsViewId = "persondetails"
        const val title = "title"
        const val version = "version"
        const val viewid = "viewid"
        const val btnRemove = "btnRemove"
        const val btnAdd = "btnAdd"
        const val list = "list"
        const val tab = "tab"
        const val add_caption = "Add"

        const val forms = "forms"
        const val fields = "fields"

        //what field from the data (a map) is the input control binding to
        const val fieldName = "fieldName"

        // needed for conversions text to int etc
        //determines what control type is used
        const val fieldDataType = "fieldDataType"
        // possible datatypes
        const val float = "float"
        const val int = "int"
        const val text = "text"
        const val lookup = "lookup"
        const val lookupKey = "lookupKey"
        const val bool = "bool"
        const val datetime = "datetime"
        const val money = "money"

        const val fieldLabelConverter = "fieldLabelConverter"
        const val required = "required"

        const val childViews = "childViews"

        fun makeColumnMapKey(fieldName: String) : String = fieldName + "_column"


    }

}
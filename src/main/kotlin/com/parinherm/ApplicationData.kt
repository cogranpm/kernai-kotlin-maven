package com.parinherm

import com.parinherm.builders.HttpClient
import com.parinherm.entity.Lookup
import com.parinherm.entity.LookupDetail
import com.parinherm.entity.schema.LookupDetailMapper
import com.parinherm.entity.schema.LookupMapper
import com.parinherm.entity.schema.SchemaBuilder
import com.parinherm.form.IFormViewModel
import com.parinherm.form.definitions.ViewDef
import com.parinherm.server.SimpleHttpServer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.eclipse.core.databinding.UpdateValueStrategy
import org.eclipse.core.databinding.observable.Realm
import org.eclipse.jface.databinding.swt.DisplayRealm
import org.eclipse.jface.resource.ImageDescriptor
import org.eclipse.jface.resource.ImageRegistry
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabItem
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.ToolItem
import com.parinherm.model.test

object ApplicationData {

    private lateinit var imageRegistry: ImageRegistry
    lateinit var mainWindow: MainWindow
    lateinit var viewDefinitions: List<ViewDef>

    /* keeps all opened tabs in a list and remove them when closed */
    var tabs: MutableMap<String, TabInstance> = mutableMapOf()

    const val IMAGE_ACTVITY_SMALL = "activitysmall"
    const val IMAGE_ACTIVITY_LARGE = "activitylarge"
    const val IMAGE_STOCK_INFO = "stock_info"
    const val IMAGE_STOCK_EXIT = "stock_exit"
    const val IMAGE_GOUP = "goup"
    const val IMAGES_PATH = "/images/"

    const val TAB_KEY_PERSON = "person"
    const val TAB_KEY_PERSONDETAIL = "persondetail"
    const val TAB_KEY_RECIPE = "recipe"
    const val TAB_KEY_INGREDIENT = "ingredient"
    const val TAB_KEY_SNIPPET = "snippet"
    const val TAB_KEY_LOGINS = "logins"
    const val TAB_KEY_NOTEBOOK = "notebook"
    const val TAB_KEY_NOTEHEADER = "noteheader"
    const val TAB_KEY_NOTEDETAIL = "notedetail"
    const val TAB_KEY_LOOKUP = "lookup"
    const val TAB_KEY_LOOKUPDETAIL = "lookupdetail"
    const val TAB_KEY_SHELF = "shelf"

    const val swnone = SWT.NONE
    const val labelStyle = SWT.BORDER
    const val listViewStyle = SWT.SINGLE or SWT.H_SCROLL or SWT.V_SCROLL or SWT.FULL_SELECTION or SWT.BORDER

    const val serverPort = "8080"
    const val serverHost = "localhost"
    const val serverProtocol = "http"
    val urls = mapOf<String, String>("views" to "views")

    //val views = ViewDefinitions.makeDefinitions()

    val defaultUpdatePolicy = UpdateValueStrategy.POLICY_UPDATE  //UpdateValueStrategy.POLICY_ON_REQUEST

    const val lookupFieldLength: Int = 20

    init {

    }

    fun start(): Unit {
        SimpleHttpServer.start()

        SchemaBuilder.build()


        val display: Display = Display.getDefault()
        Realm.runWithDefault(DisplayRealm.getRealm(display)) {
            try {
                viewDefinitions = getSerializationFormat().decodeFromString(HttpClient.getViews())
                imageRegistry = ImageRegistry()
                lookups = LookupMapper.getLookups()

                //testing strintemplate
                test(ApplicationData.getView(ApplicationData.ViewDefConstants.shelfViewId))


                mainWindow = MainWindow(null)
                mainWindow.setBlockOnOpen(true)
                mainWindow.open()
                Display.getCurrent().dispose()
            } catch (ex: Exception) {
                println(ex.message)
            }
        }
        SimpleHttpServer.stop()
    }

    fun getSerializationFormat() = Json { prettyPrint = true }



    /************************** new way ***************************************************/
    fun makeTab(viewModel: IFormViewModel<*>, caption: String, key: String): Unit {
        if (tabs.containsKey(key) && tabs[key] != null) {
            if (tabs[key]!!.isClosed) {
                // set it to open and create the tab
                tabs[key] = createTab(viewModel, caption, key)
            } else {
                // set focus to existing tab somehow

            }
        } else {
            tabs[key] = createTab(viewModel, caption, key)
        }
    }

    private fun createTab(viewModel: IFormViewModel<*>, caption: String, key: String): TabInstance {
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


    /*
    fun getView(viewId: String): Map<String, Any> {
        val forms: List<Map<String, Any>> = views[ViewDefConstants.forms] as List<Map<String, Any>>
        return forms.first { it[ViewDefConstants.viewid] == viewId }
    }

     */

    fun getView(viewId: String): ViewDef =
        viewDefinitions.first {it.id == viewId}


    fun makeServerUrl(urlKey: String): String = "$serverProtocol://$serverHost:$serverPort/${urls[urlKey]}"

    fun setupImages() {
        try {
            putImage(IMAGE_ACTVITY_SMALL, "Activity_16xSM.png")
            putImage(IMAGE_ACTIVITY_LARGE, "Activity_32x.png")
            putImage(IMAGE_GOUP, "go-up.png")
            putImage(IMAGE_STOCK_EXIT, "stock_exit_24.png")
            putImage(IMAGE_STOCK_INFO, "stock_save_24.png")
        } catch (e: Exception) {
            println(e)
        }
    }

    private fun putImage(key: String, filename: String) = try {
        val path: String = IMAGES_PATH + filename
        this.imageRegistry.put(key, ImageDescriptor.createFromFile(ApplicationData.javaClass, path))
    } catch (e: Exception) {
        println(e)
    }

    fun getImage(name: String): Image {
        return this.imageRegistry.get(name)
    }

    const val countryLookupKey = "country"
    const val speciesLookupKey = "species"
    const val recipeCategoryLookupKey = "recipecat"
    const val unitLookupKey = "unit"
    const val techLanguageLookupKey = "techlang"
    const val snippetCategoryKey = "cat"
    const val snippetTopicKey = "topic"
    const val snippetTypeKey = "type"
    const val passwordMasterKey = "password_master"
    const val loginCategoryKey = "logcat"

    fun createLookups(){
        lookups.forEach { (key: String, value: List<LookupDetail>) ->
            createLookup(key, key, value)
        }
    }

    fun createLookup(key: String, name: String, items: List<LookupDetail>){
        val lookup = Lookup(0, key, name)
        LookupMapper.save(lookup)
        items.forEach {
            it.lookupId = lookup.id
            LookupDetailMapper.save(it)
        }
    }


    val countryList: List<LookupDetail> by lazy { lookups.getOrDefault(countryLookupKey, emptyList())}
    val speciesList: List<LookupDetail> by lazy { lookups.getOrDefault(speciesLookupKey, emptyList())}
    val recipeCategoryList by lazy { lookups.getOrDefault(recipeCategoryLookupKey, emptyList())}
    val unitList: List<LookupDetail> by lazy {lookups.getOrDefault(unitLookupKey, emptyList())}
    val techLanguage: List<LookupDetail> by lazy {lookups.getOrDefault(techLanguageLookupKey, emptyList())}
    val snippetCategory: List<LookupDetail> by lazy {lookups.getOrDefault(snippetCategoryKey, emptyList())}
    val snippetTopic: List<LookupDetail> by lazy {lookups.getOrDefault(snippetTopicKey, emptyList())}
    val snippetType: List<LookupDetail> by lazy {lookups.getOrDefault(snippetTypeKey, emptyList())}
    val passwordMaster: List<LookupDetail> by lazy {lookups.getOrDefault(passwordMasterKey, emptyList())}
    val loginCategoryList by lazy {lookups.getOrDefault(loginCategoryKey, emptyList())}

    lateinit var lookups: Map<String, List<LookupDetail>>

    fun getSaveToolbarButton() : ToolItem{
        return mainWindow.toolBarManager.control.getItem(0)
    }


    fun getNewToolbarButton() : ToolItem{
        return mainWindow.toolBarManager.control.getItem(1)
    }


    fun getDeleteToolbarButton() : ToolItem{
        return mainWindow.toolBarManager.control.getItem(2)
    }


    object ViewDefConstants {

        // the view id's
        const val personViewId = "beansbinding"
        const val personDetailsViewId = "persondetails"
        const val recipeViewId = "recipe"
        const val ingredientViewId = "ingredients"
        const val techSnippetsViewId = "techsnip"
        const val loginViewId = "login"
        const val notebookViewId = "notebook"
        const val noteheaderViewId = "noteheader"
        const val noteDetailViewId = "notedetail"
        const val lookupViewId = "lookup"
        const val lookupDetailViewId = "lookupdetail"
        const val shelfViewId = "shelf"

       // the properties available to the views
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

        // memo is long text
        const val memo = "memo"
        const val lookup = "lookup"
        const val lookupKey = "lookupKey"
        const val bool = "bool"
        const val datetime = "datetime"
        const val money = "money"
        // source is a source code editor
        const val source = "source"

        // sizing
        const val sizeHint = "sizeHint"
        const val large = "large"
        const val medium = "medium"
        const val small = "small"

        const val fieldLabelConverter = "fieldLabelConverter"
        const val required = "required"
        const val listWeight = "listweight"
        const val editWeight = "editweight"
        const val sashOrientation = "sashorientation"
        const val horizontal = "horizontal"
        const val vertical = "vertical"

        const val childViews = "childViews"

        fun makeColumnMapKey(fieldName: String): String = fieldName + "_column"

    }

}
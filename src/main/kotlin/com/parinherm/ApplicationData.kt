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
import org.eclipse.swt.widgets.ToolItem

object ApplicationData {

    private lateinit var imageRegistry: ImageRegistry
    lateinit var mainWindow: MainWindow
    lateinit var viewDefinitions: Map<String, Any>

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

    const val swnone = SWT.NONE
    const val labelStyle = SWT.BORDER
    const val listViewStyle = SWT.SINGLE or SWT.H_SCROLL or SWT.V_SCROLL or SWT.FULL_SELECTION or SWT.BORDER

    const val serverPort = "8080"
    const val serverHost = "localhost"
    const val serverProtocol = "http"
    val urls = mapOf<String, String>("views" to "views")

    val views = ViewDefinitions.makeDefinitions()

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
                viewDefinitions = getViewDefinitions()
                imageRegistry = ImageRegistry()
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


    fun getView(viewId: String): Map<String, Any> {
        val forms: List<Map<String, Any>> = views[ViewDef.forms] as List<Map<String, Any>>
        return forms.first { it[ViewDef.viewid] == viewId }
    }

    fun getView(viewId: String, viewDefinitions: Map<String, Any>): Map<String, Any> {
        val forms: List<Map<String, Any>> = viewDefinitions[ViewDef.forms] as List<Map<String, Any>>
        return forms.first { it[ViewDef.viewid] == viewId }
    }

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

    val recipeCategoryList = listOf(
        LookupDetail("instant", "Instant Pot"),
        LookupDetail("main", "Main Course"),
        LookupDetail("dessert", "Dessert"),
        LookupDetail("soup", "Soup"),
        LookupDetail("mama", "Ma Ma")
    )

    val unitList: List<LookupDetail> = listOf(
        LookupDetail("cup", "Cup"),
        LookupDetail("tbl", "Tablespoon"),
        LookupDetail("tsp", "Teaspoon"),
        LookupDetail("can", "Can"),
        LookupDetail("whl", "Whole"),
        LookupDetail("pnd", "Pound"),
        LookupDetail("pch", "Pinch"),
        LookupDetail("oz", "Ounce"),
        LookupDetail("dp", "Drop"),
        LookupDetail("hnt", "Hint"),
        LookupDetail("prt", "Part"),
        LookupDetail("jar", "Jar"),
        LookupDetail("pkt", "Packet")
    )


    val techLanguage: List<LookupDetail> = listOf(
        LookupDetail("kot", "Kotlin")
    )

    val snippetCategory: List<LookupDetail> = listOf(
        LookupDetail("gen", "General"),
        LookupDetail("book", "Books"),
        LookupDetail("stdlib", "Standard Library")
    )

    val snippetTopic: List<LookupDetail> = listOf(
        LookupDetail("o", "Other"),
        LookupDetail("func", "Functional Programming"),
        LookupDetail("db", "Database"),
        LookupDetail("col", "Collections")
    )

    val snippetType: List<LookupDetail> = listOf(
        LookupDetail("eg", "Example"),
        LookupDetail("tr", "Training")
    )

    val passwordMaster: List<LookupDetail> = listOf(
        LookupDetail("rd", "red dog"),
        LookupDetail("rd123", "red dog 123"),
        LookupDetail("rd123!", "red dog 123!"),
        LookupDetail("rd!23", "red dog !23"),
        LookupDetail("Rd!23", "Red dog !23"),
        LookupDetail("1xpq", "1xp  q"),
        LookupDetail("1xpm", "1xp  m"),
        LookupDetail("1xp0", "1xp  0"),
        LookupDetail("1xpM0q", "1xp q (no punctuation)" ),
        LookupDetail("mediacom", "Mediacom!"),
        LookupDetail("emmers", "emmers2425"),
        LookupDetail("emmett", "Emmett2425"),
        LookupDetail("mystery", "Mystery5570"),
        LookupDetail("Mystery5570!", "Mystery5570!"),
        LookupDetail("n/a", "Not Applicable"),
        LookupDetail("cambridge", "cambridge")

    )

    val loginCategoryList = listOf(
        LookupDetail("gen", "General"),
        LookupDetail("fin", "Financial"),
        LookupDetail("gov", "Government"),
        LookupDetail("tra", "Travel"),
        LookupDetail("misc", "Misc")
    )


    val lookups: Map<String, List<LookupDetail>> = mapOf(
        countryLookupKey to countryList,
        speciesLookupKey to speciesList,
        recipeCategoryLookupKey to recipeCategoryList,
        unitLookupKey to unitList,
        techLanguageLookupKey to techLanguage,
        snippetCategoryKey to snippetCategory,
        snippetTopicKey to snippetTopic,
        snippetTypeKey to snippetType,
        passwordMasterKey to passwordMaster,
        loginCategoryKey to loginCategoryList
    )

    fun getSaveToolbarButton() : ToolItem{
        return mainWindow.toolBarManager.control.getItem(0)
    }


    fun getNewToolbarButton() : ToolItem{
        return mainWindow.toolBarManager.control.getItem(1)
    }


    fun getDeleteToolbarButton() : ToolItem{
        return mainWindow.toolBarManager.control.getItem(2)
    }


    object ViewDef {

        // the view id's
        const val bindingTestViewId = "bindingtest"
        const val personViewId = "beansbinding"
        const val personDetailsViewId = "persondetails"
        const val recipeViewId = "recipe"
        const val ingredientViewId = "ingredients"
        const val techSnippetsViewId = "techsnip"
        const val loginViewId = "login"
        const val notebookViewId = "notebook"
        const val noteheaderViewId = "noteheader"
        const val noteDetailViewId = "notedetail"

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

        const val fieldLabelConverter = "fieldLabelConverter"
        const val required = "required"
        const val sashWeights = "sashweight"
        const val sashOrientation = "sashorientation"
        const val horizontal = "horizontal"
        const val vertical = "vertical"

        const val childViews = "childViews"

        fun makeColumnMapKey(fieldName: String): String = fieldName + "_column"


    }

}
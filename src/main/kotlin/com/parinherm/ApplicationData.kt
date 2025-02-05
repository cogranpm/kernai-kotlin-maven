package com.parinherm

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.HumanizeHelper
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import com.parinherm.audio.AudioClient
import com.parinherm.audio.SpeechRecognition
import com.parinherm.entity.AppVersion
import com.parinherm.entity.FieldDefinition
import com.parinherm.entity.ViewDefinition
import com.parinherm.entity.schema.AppVersionMapper
import com.parinherm.entity.schema.FieldDefinitionMapper
import com.parinherm.entity.schema.SchemaBuilder
import com.parinherm.entity.schema.ViewDefinitionMapper
import com.parinherm.font.FontUtils
import com.parinherm.form.definitions.*
import com.parinherm.form.dialogs.FirstTimeSetupDialog
import com.parinherm.form.widgets.ViewPicker
import com.parinherm.image.ImageUtils
import com.parinherm.lookups.LookupUtils
import com.parinherm.model.TemplateHelpers
import com.parinherm.script.DecapitalizeExtension
import com.parinherm.script.RemoveQuotesExtension
import com.parinherm.security.Cryptographer
import com.parinherm.settings.Setting
import com.parinherm.settings.SettingsDialog
import io.pebbletemplates.pebble.PebbleEngine
import io.pebbletemplates.pebble.loader.FileLoader
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory
import org.eclipse.core.databinding.UpdateValueStrategy
import org.eclipse.core.databinding.observable.Realm
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.MultiStatus
import org.eclipse.core.runtime.Status
import org.eclipse.jface.databinding.swt.DisplayRealm
import org.eclipse.jface.dialogs.ErrorDialog
import org.eclipse.jface.resource.JFaceResources
import org.eclipse.jface.window.Window
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.ToolItem
import java.io.File
import java.nio.file.Files
import java.util.*
import kotlin.system.exitProcess


object ApplicationData {

    const val version = 1.0

    lateinit var mainWindow: MainWindow
    lateinit var userPath: String

    /* keeps all opened tabs in a list and remove them when closed */
    var tabs: MutableMap<String, TabInstance> = mutableMapOf()

    const val APPLICATION_NAME = "COUP"
    const val APPLICATION_DISPLAY_NAME = "Compendium Of Useful Programs"
    const val VENDOR_NAME = "Parinherm"
    const val SCRIPT_PATH = "scripts"
    val pluginId = "${VENDOR_NAME}${APPLICATION_NAME}"
    const val TEMPLATE_PATH = "templates"
    const val PLUGIN_TEMPLATE_PATH = "plugin"
    const val DOTNET_TEMPLATE_PATH = "dotnet"
    const val DATA_PATH = "data"
    const val LOG_PATH = "logs"

    val logger = LogManager.getLogger()

    const val swnone = SWT.NONE
    const val labelStyle = SWT.NONE
    const val listViewStyle = SWT.SINGLE or SWT.H_SCROLL or SWT.V_SCROLL or SWT.FULL_SELECTION or SWT.BORDER

    val defaultUpdatePolicy = UpdateValueStrategy.POLICY_UPDATE  //UpdateValueStrategy.POLICY_ON_REQUEST

    const val defaultSashWidth = 4
    const val dbTypeEmbedded = "embed"
    const val dbTypeMySql = "mysql"
    const val dbTypeSqlite = "sqlite"
    const val dbTypePostgres = "postgres"
    const val dbKeyUrl = "db.url"
    const val dbKeyType = "db.type"
    const val dbKeyDriver = "db.driver"
    const val dbKeyUser = "db.user"
    const val dbKeyPassword = "db.password"
    const val encryptionSecretKey = "encryptionSecret"
    const val propertiesFile = "config.properties"
    const val scriptExtension = ".kts"
    const val bootstrapScriptFileName = "bootstrap$scriptExtension"
    const val maxRowsLimit = 10000

    lateinit var pebbleEngine: PebbleEngine
    lateinit var handleBarsEngine: Handlebars

    init {
        createUserPath()
    }

    fun mapViewDefinitionToViewDef(
        viewDefinition: ViewDefinition,
        fields: List<FieldDefinition>,
        childViews: List<ViewDef>
    ): ViewDef {
        //if (viewDefinition != null) {
        val def = ViewDef(
            viewDefinition.id,
            viewDefinition.viewId,
            viewDefinition.title,
            viewDefinition.listWeight,
            viewDefinition.editWeight,
            SashOrientationDef.unMappedOrientation(viewDefinition.sashOrientation),
            fields.map { mapFieldDefinitionToFieldDef(it) },
            viewDefinition.config,
            EntityDef(viewDefinition.entityName),
            childViews,
            false,
            mutableListOf()
        )
        for (fieldDefinition in def.fieldDefinitions) {
            if (fieldDefinition.referenceViewDefinition != null) {
                if (!def.referenceViewsList.contains(fieldDefinition.referenceViewDefinition)) {
                    def.referenceViewsList.add(fieldDefinition.referenceViewDefinition);
                }
            }
        }
        for (viewDefinition in def.childViews) {
            for (fieldDefinition in viewDefinition.fieldDefinitions) {
                if (fieldDefinition.referenceViewDefinition != null) {
                    if (!def.referenceViewsList.contains(fieldDefinition.referenceViewDefinition)) {
                        def.referenceViewsList.add(fieldDefinition.referenceViewDefinition);
                    }
                }
            }
        }
        return def;
    }


    fun mapFieldDefinitionToFieldDef(fieldDefinition: FieldDefinition): FieldDef {
        val fieldDef = FieldDef(
            fieldDefinition.name,
            fieldDefinition.title,
            fieldDefinition.required,
            SizeDef.unMappedSize(fieldDefinition.size),
            DataTypeDef.unMappedDataType(fieldDefinition.dataType),
            fieldDefinition.lookupKey,
            fieldDefinition.filterable,
            fieldDefinition.default,
            fieldDefinition.config,
            fieldDefinition.sequence,
            fieldDefinition.length ?: 0,
            when (fieldDefinition.referenceViewId != null && fieldDefinition.referenceViewId!! > 0){
                true ->  ViewPicker.dataSource.find { it.id == fieldDefinition.referenceViewId }
                else -> null
            },
            ReferenceDef(EntityDef(""))
        )
        if(fieldDefinition.referenceViewId != null && fieldDefinition.referenceViewId!! > 0){
            println(fieldDefinition.name)
        }
        return fieldDef
    }

    fun loadChildViews(parentViewId: Long): List<ViewDef> {
        val views = ViewDefinitionMapper.getAllByParent(mapOf("viewDefinitionId" to parentViewId))
        return views.map {
            val fields = FieldDefinitionMapper.getAll(mapOf("viewDefinitionId" to it.id))
            ApplicationData.mapViewDefinitionToViewDef(it, fields, loadChildViews(it.id))
        }
    }


    private fun createUserPath() {
        userPath = System.getProperty("user.home") +
                File.separator +
                "Application Data" +
                File.separator +
                ApplicationData.VENDOR_NAME +
                File.separator +
                ApplicationData.APPLICATION_NAME +
                File.separator
        val directory: File = File(userPath)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        createUserDirectory("${userPath}${TEMPLATE_PATH}${File.separator}${version}")
        createUserDirectory("${userPath}${TEMPLATE_PATH}${File.separator}${version}${File.separator}${PLUGIN_TEMPLATE_PATH}")
        createUserDirectory("${userPath}${TEMPLATE_PATH}${File.separator}${version}${File.separator}${PLUGIN_TEMPLATE_PATH}${File.separator}${DOTNET_TEMPLATE_PATH}")
        createUserDirectory("${userPath}${SCRIPT_PATH}")
        createUserDirectory("${userPath}${ImageUtils.IMAGES_PATH}")
        createUserDirectory("${userPath}${DATA_PATH}")
        createUserDirectory("${userPath}${LOG_PATH}")
    }

    private fun createUserDirectory(path: String) {
        val directory: File = File(path)
        if (!directory.exists()) {
            directory.mkdirs()
        }
    }

    fun makeCapital(source: String) =
        source.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    fun decapitalize(source: String) =
        source.replaceFirstChar { if (it.isUpperCase()) it.lowercase(Locale.getDefault()) else it.toString() }


    /**********************************
     * startup code
     */
    suspend fun startupTasks() {
    }

    fun start(): Unit {
        //Display.setAppName(APPLICATION_NAME)
        val display: Display = Display.getDefault()
        // note that db operations cannot be performed in background thread
        // exposed framework uses the current thread internally

        Realm.runWithDefault(DisplayRealm.getRealm(display)) {
            try {
                configureLogging()
                ImageUtils.setupImages()
                FontUtils.setupFonts()
                copyTemplates()
                initializeTemplateEngines()
                JFaceResources.getFontRegistry()
                    .put(JFaceResources.BANNER_FONT, FontUtils.getFont(FontUtils.FONT_IBM_PLEX_MONO_HEADER).fontData)
                //ui is not database connected as yet
                mainWindow = MainWindow(null)
                mainWindow.setBlockOnOpen(true)
                mainWindow.open()
                display.dispose()
            } catch (ex: Exception) {
                logError(ex, "Error in main loop")
                showErrorDialog("Error opening Main Window, the program must now exit", ex)
            }
        }
    }

    private fun configureLogging() {
        /* this is hard to get right
        https://www.studytonight.com/post/log4j2-programmatic-configuration-in-java-class
         */
        val logFile = "${userPath}${LOG_PATH}${File.separator}app"
        val pattern = "%d %p %c [%t] %m%n"
        val builder = ConfigurationBuilderFactory.newConfigurationBuilder()
        builder.setStatusLevel(Level.DEBUG)
        builder.setConfigurationName("DefaultRollingFileLogger")
        val layoutBuilder = builder.newLayout("PatternLayout")
            .addAttribute("pattern", pattern)
        /*
        val triggeringPolicy: ComponentBuilder<*> = builder.newComponent<ComponentBuilder<*>>("Policies")
            .addComponent(
                builder.newComponent<ComponentBuilder<*>>("SizeBasedTriggeringPolicy").addAttribute("size", "10MB")
            )
         */

        val triggeringPolicy = builder.newComponent("Policies")
            .addComponent(builder.newComponent("CronTriggeringPolicy").addAttribute("schedule", "0 0 0 * * ?"))
            .addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "10M"))

        val appenderBuilder: AppenderComponentBuilder = builder.newAppender("LogToRollingFile", "RollingFile")
            .addAttribute("fileName", "${logFile}.log")
            .addAttribute("filePattern", "${logFile}-%d{MM-dd-yy-HH-mm-ss}.log")
            .add(layoutBuilder)
            .addComponent(triggeringPolicy)

        builder.add(appenderBuilder)
        val rootLogger = builder.newRootLogger(Level.ALL)
        rootLogger.add(builder.newAppenderRef("LogToRollingFile"))
        builder.add(rootLogger)
        Configurator.reconfigure(builder.build())
    }

    fun startupRoutine(display: Display, setting: Setting, forceSettingsDisplay: Boolean) {
        if (!verifySettings(display, setting, forceSettingsDisplay)) {
            display.dispose()
            exitProcess(0)
        }
        Cryptographer.setSecret(setting?.encryptionSecret ?: "")
        verifyConnection(display, setting)
    }

    private fun verifySettings(display: Display, setting: Setting, forceSettingsDisplay: Boolean): Boolean {
        setting.read()
        var dbType = setting.dbType
        if (dbType.isNullOrBlank() || forceSettingsDisplay) {
            //presume first time in setting up
            val dialog = SettingsDialog(display.activeShell)
            if (dialog.open() != Window.OK) {
                showErrorDialog("Settings cancelled, the program must now exit", null)
                return false
            }
            setting.read()
        }
        dbType = setting.dbType
        if (dbType.isNullOrBlank()) {
            //gave 1 chance, now we should exit
            showErrorDialog("Settings are invalid, Database Type not set", null)
        }
        return true
    }


    private fun verifyConnection(display: Display, setting: Setting) {
        val (didConnect, message) = connect(setting)
        if (!didConnect) {
            showErrorDialog("Database Connection Error, please check settings. Message: ${message}", null)
            //go around again
            startupRoutine(display, setting, true)
        }
    }

    fun initializeTemplateEngines() {
        val fileLoader = FileLoader()
        fileLoader.prefix =
            "${userPath}${TEMPLATE_PATH}${File.separator}${version}${File.separator}${PLUGIN_TEMPLATE_PATH}${File.separator}${DOTNET_TEMPLATE_PATH}"
        this.pebbleEngine = PebbleEngine.Builder()
            .cacheActive(false)
            .templateCache(null)
            .tagCache(null)
            .autoEscaping(false)
            .loader(fileLoader)
            .extension(DecapitalizeExtension())
            .extension(RemoveQuotesExtension())
            .build()

        val loader = ClassPathTemplateLoader()
        loader.prefix = "/${TEMPLATE_PATH}/${PLUGIN_TEMPLATE_PATH}"
        this.handleBarsEngine = Handlebars(loader)
        this.handleBarsEngine.registerHelpers(TemplateHelpers())
        HumanizeHelper.register(this.handleBarsEngine)

    }

    fun copyTemplates() {
        copyTemplate("/$TEMPLATE_PATH/", "", "entity.hbs")
        copyTemplate("/$TEMPLATE_PATH/", "", "mapper.hbs")
        copyTemplate("/$TEMPLATE_PATH/", "", "schema.hbs")
        copyTemplate("/$TEMPLATE_PATH/", "", "view.hbs")
        copyTemplate("/$TEMPLATE_PATH/", "", "viewModel.hbs")
        copyTemplate("/$TEMPLATE_PATH/", "", "bootstrap.hbs")

        //plugin templates
        val outputSuffixPlugins = "${PLUGIN_TEMPLATE_PATH}${File.separator}"
        val basePathPlugins = "${TEMPLATE_PATH}/${PLUGIN_TEMPLATE_PATH}"
        copyTemplate(basePathPlugins, outputSuffixPlugins, "entity.hbs")
        copyTemplate(basePathPlugins, outputSuffixPlugins, "mapper.hbs")
        copyTemplate(basePathPlugins, outputSuffixPlugins, "schema.hbs")
        copyTemplate(basePathPlugins, outputSuffixPlugins, "view.hbs")
        copyTemplate(basePathPlugins, outputSuffixPlugins, "viewModel.hbs")

        val outputSuffixPluginsDotnet =
            "${PLUGIN_TEMPLATE_PATH}${File.separator}${DOTNET_TEMPLATE_PATH}${File.separator}"
        val basePathPluginsDotnet = "/${TEMPLATE_PATH}/${PLUGIN_TEMPLATE_PATH}/${DOTNET_TEMPLATE_PATH}"
        copyTemplate(basePathPluginsDotnet, outputSuffixPluginsDotnet, "dto.peb")
        copyTemplate(basePathPluginsDotnet, outputSuffixPluginsDotnet, "endpoint.peb")
        copyTemplate(basePathPluginsDotnet, outputSuffixPluginsDotnet, "form.peb")
        copyTemplate(basePathPluginsDotnet, outputSuffixPluginsDotnet, "functions.peb")
        copyTemplate(basePathPluginsDotnet, outputSuffixPluginsDotnet, "repositoryClass.peb")
        copyTemplate(basePathPluginsDotnet, outputSuffixPluginsDotnet, "repositoryInterface.peb")
        copyTemplate(basePathPluginsDotnet, outputSuffixPluginsDotnet, "viewModel.peb")
    }

    private fun copyTemplate(basePath: String, outputSuffix: String, fileName: String) {
        /* copy all the templates from the resources directory to the user folder */
        val templatePath: String = "/$basePath/$fileName"
        val outputFile =
            File("$userPath${File.separator}$TEMPLATE_PATH${File.separator}${version}${File.separator}${outputSuffix}$fileName")
        if (!outputFile.exists()) {
            try {
                val filestream = this.javaClass.getResourceAsStream(templatePath).use {
                    Files.copy(it, outputFile.toPath())
                };
            } catch (e: Exception) {
                ApplicationData.logError(e, "Error copying template $fileName Message: ${e.message}")
            }
        }
    }

    fun reconnect() {
        try {
            val setting: Setting = Setting("", "", "", "", "", "")
            setting.read()
            val (result, message) = connect(setting)
            if (!result) {
                val errMessage = "Error connecting to database: ${message}. Please check the settings."
                showErrorDialog(errMessage, null)
            }
        } catch (e: Exception) {
            val errMessage = "Error connecting to database: ${e.message}. Please check the settings."
            showErrorDialog(errMessage, e)
        }
    }

    fun showErrorDialog(errMessage: String, exception: Exception?, title: String = "Error") {
        ErrorDialog.openError(
            Display.getDefault().activeShell,
            title,
            errMessage,
            exception?.let
            { createMultiStatus(errMessage, exception) }
                ?: Status(IStatus.ERROR, pluginId, errMessage)
        )
    }

    private fun createMultiStatus(errMessage: String, e: Exception): MultiStatus {
        val list: List<Status> = listOf(
            Status(IStatus.ERROR, pluginId, errMessage),
            Status(IStatus.ERROR, pluginId, e.message)
        )
        return MultiStatus(pluginId, IStatus.ERROR, list.toTypedArray(), e.toString(), e)
    }

    private fun connect(setting: Setting): Pair<Boolean, String> {
        return try {
            SchemaBuilder.db = SchemaBuilder.connect(setting)
            if (SchemaBuilder.db == null) {
                false to "Database is null"
            } else {
                //this should trigger an error if database is invalid
                val dbVersion = SchemaBuilder.db?.version
                checkVersion()
                SchemaBuilder.updateSchema()
                true to ""
            }
        } catch (e: Exception) {
            val msg = e.message ?: "unknown"
            logError(e, msg)
            false to msg
        }
    }

    fun testConnection(setting: Setting): Pair<Boolean, Exception?> {
        return try {
            val db = SchemaBuilder.connect(setting)
            //have to run a query to find out if database really exists
            val dbVersion = db?.version
            Pair(true, null)
        } catch (e: Exception) {
            Pair(false, e)
        }
    }

    /*
        suspend fun testConnection(setting: Setting) : Pair<Boolean, Exception?> {
            return try {
                val db = SchemaBuilder.connect(setting)
                Pair(true, null)
            } catch(e: Exception){
                Pair(false, e)
            }
        }
     */

    private fun checkVersion() {
        var versionList = listOf<AppVersion>()
        try {
            versionList = AppVersionMapper.getAll(mapOf())
        } catch (e: Exception) {
            //presumably the schema's do not yet exist
        }
        if (versionList.isEmpty()) {
            // 1 time setup stuff
            val firstTimeDialog = FirstTimeSetupDialog(Display.getDefault().activeShell)
            firstTimeDialog.open()
        }
        LookupUtils.load()
    }


    fun getSaveToolbarButton(): ToolItem {
        return mainWindow.toolBarManager.control.getItem(0)
    }

    fun getNewToolbarButton(): ToolItem {
        return mainWindow.toolBarManager.control.getItem(1)
    }


    fun getDeleteToolbarButton(): ToolItem {
        return mainWindow.toolBarManager.control.getItem(2)
    }

    fun logError(e: Exception?, moreInfo: String?) {
        logger.error(moreInfo, e)
    }

    fun close() {
        try {
            AudioClient.close()
            SpeechRecognition.close()
            SchemaBuilder.close()
        } catch (e: Exception) {
            logger.error("Error in closing down", e)
        }

    }
}


//const val serverPort = 8082
//const val serverHost = "localhost"
//const val serverProtocol = "http"
//val urls = mapOf<String, String>("views" to "views")

//val views = ViewDefinitions.makeDefinitions()


/* now this is removed, using database instead of a http server
SimpleHttpServer.start()
var viewsPacket = HttpClient.getViews()
if(viewsPacket is Result.Success) {
    viewDefinitions = getSerializationFormat().decodeFromString(viewsPacket.data)
} else {
    if (viewsPacket is Result.Error) {
        println(viewsPacket.e)
    }
}
 */

/*
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
    const val TAB_KEY_SUBJECT = "subject"
    const val TAB_KEY_PUBLICATION = "publication"
    const val TAB_KEY_TOPIC = "topic"
    const val TAB_KEY_NOTE = "note"
    const val TAB_KEY_NOTESEGMENT = "notesegment"
    const val TAB_KEY_NOTESEGMENTTYPEHEADER = "notesegmenttypeheader"
    const val TAB_KEY_NOTESEGMENTTYPE = "notesegmenttype"
    const val TAB_KEY_QUIZ = "quit"
    const val TAB_KEY_QUESTION = "question"
    const val TAB_KEY_ANSWER = "answer"
    const val TAB_KEY_QUIZRUNHEADER = "quizrunheader"
    const val TAB_KEY_QUIZRUNQUESTION = "quizrunquestion"
    const val TAB_KEY_VIEWDEFINITION = "viewdefinition"
    const val TAB_KEY_VIEWDEFINITIONCHILD = "viewdefinitionchild"
    const val TAB_KEY_FIELDDEFINITION = "fielddefinition"
 */

/*
fun makeTab(viewModel: IFormViewModel<*>, key: String, tabInfo: TabInfo){
    tabs[key] = createTab(viewModel, tabInfo.caption, key, tabInfo.imageKey, tabInfo.fontKey)
}
 */

//fun makeTab(viewModel: IFormViewModel<*>, caption: String, key: String, imageKey: String? = null, fontKey: String? = null): Unit {
/* stop worry about double opening tabs
if (tabs.containsKey(key) && tabs[key] != null) {
    if (tabs[key]!!.isClosed) {
        // set it to open and create the tab
        tabs[key] = createTab(viewModel, caption, key, imageKey)
    } else {
        // set focus to existing tab somehow

    }
} else {
    tabs[key] = createTab(viewModel, caption, key, imageKey)
}
 */
//   tabs[key] = createTab(viewModel, caption, key, imageKey, fontKey)
//}

/*
fun makeFileEditorTab(viewModel: IFileViewModel, caption: String, key: String) {
    createFileEditorTab(viewModel, caption, key)
}
 */

/*
private fun createTab(viewModel: IFormViewModel<*>, caption: String, key: String, imageKey: String?, fontKey: String?): TabInstance {
    val tabItem = CTabItem(mainWindow.folder, SWT.CLOSE)
    tabItem.text = caption
    tabItem.control = viewModel.render()
    tabItem.addDisposeListener {
        tabs[key]!!.isClosed = true
    }
    tabItem.setData("key", key)
    if (imageKey != null) {
        tabItem.image = ImageUtils.getImage(imageKey)
    }
    if(fontKey != null){
        tabItem.font = FontUtils.getFont(fontKey)
    }
    mainWindow.folder.selection = tabItem
    return TabInstance(viewModel, tabItem, false)
}
private fun createFileEditorTab(viewModel: IFileViewModel, caption: String, key: String) {
    val tabItem = CTabItem(mainWindow.folder, SWT.CLOSE)
    tabItem.text = caption
    tabItem.control = viewModel.render()
    tabItem.setData("key", key)
    mainWindow.folder.selection = tabItem
}

fun getView(viewId: String): ViewDef =
    viewDefinitions.first { it.id == viewId }


//fun makeServerUrl(urlKey: String): String = "$serverProtocol://$serverHost:$serverPort/${urls[urlKey]}"

 */

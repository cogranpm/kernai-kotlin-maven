package com.parinherm.settings

import com.parinherm.ApplicationData
import org.eclipse.jface.dialogs.TitleAreaDialog
import org.eclipse.jface.dialogs.IMessageProvider
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*

import com.parinherm.ApplicationData.dbKeyPassword
import com.parinherm.ApplicationData.dbKeyType
import com.parinherm.ApplicationData.dbKeyUrl
import com.parinherm.ApplicationData.dbKeyUser
import com.parinherm.ApplicationData.dbTypeEmbedded
import com.parinherm.ApplicationData.dbTypeMySql
import com.parinherm.ApplicationData.dbTypePostgres
import com.parinherm.ApplicationData.dbTypeSqlite
import com.parinherm.ApplicationData.encryptionSecretKey
import com.parinherm.image.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swt.SWT
import org.eclipse.swt.layout.RowLayout

class SettingsDialog(parentShell: Shell?) : TitleAreaDialog(parentShell) {

    private val setting: Setting = Setting("", "", "", "", "", "")
    private lateinit var widgetsMap: Map<String, Text>
    private lateinit var radioMap: Map<String, Button>
    private lateinit var lblError: Label
    private lateinit var lblAssistance: Label
    var reconnect = false

    init {
    }

    override fun create() {
        super.create()
        // initial setup here
        setting.read()
        setMessage("Settings", IMessageProvider.INFORMATION)
        widgetsMap[dbKeyType]?.text = setting?.dbType ?: ""
        widgetsMap[dbKeyUrl]?.text = setting?.dbUrl ?: ""
        widgetsMap[dbKeyUser]?.text = setting?.dbUser ?: ""
        widgetsMap[dbKeyPassword]?.text = setting?.dbPassword ?: ""
        widgetsMap[encryptionSecretKey]?.text = setting?.encryptionSecret ?: ""

        when (setting.dbType) {
            dbTypeMySql -> {
                radioMap[dbTypeMySql]?.selection = true
                setClientServerOptions()
                setHelp(dbTypeMySql)
            }
            dbTypeEmbedded -> {
                radioMap[dbTypeEmbedded]?.selection = true
                setEmbeddedOptions()
                setHelp(dbTypeEmbedded)
            }
            dbTypeSqlite -> {
                radioMap[dbTypeSqlite]?.selection = true
                setNoAuthOptions()
                setHelp(dbTypeSqlite)
            }
            dbTypePostgres -> {
                radioMap[dbTypePostgres]?.selection = true
                setClientServerOptions()
                setHelp(dbTypePostgres)
            }
        }
    }

    override fun isResizable(): Boolean {
        return true
    }


    private fun setFieldValue(key: String, oldValue: String, setter: (String) -> Unit): Boolean {
        val widget = widgetsMap.getOrDefault(key, null)
        val widgetValue = if (widget != null) {
            widget.text
        } else {
            ""
        }
        return if (oldValue != widgetValue) {
            setter(widgetValue)
            true
        } else false
    }

    private fun getRadioSelection(key: String): Boolean {
        val radio = radioMap.getOrDefault(key, null)
        val radioValue = if (radio != null) {
            radio.selection
        } else {
            false
        }
        return radioValue
    }

    private fun transferFromForm(): Pair<Boolean, Boolean> {
        val isEmbedded = getRadioSelection(dbTypeEmbedded)
        val isMysql = getRadioSelection(dbTypeMySql)
        val isPostgres = getRadioSelection(dbTypePostgres)
        val isSqlite = getRadioSelection(dbTypeSqlite)
        var reconnectRequired = false
        var writeRequired = false
        if (isEmbedded) {
            reconnectRequired = (setting.dbType != dbTypeEmbedded)
            setting.dbType = dbTypeEmbedded
        } else if (isMysql) {
            reconnectRequired = (setting.dbType != dbTypeMySql)
            setting.dbType = dbTypeMySql
        } else if (isPostgres) {
            reconnectRequired = (setting.dbType != dbTypePostgres)
            setting.dbType = dbTypePostgres
        } else if (isSqlite) {
            reconnectRequired = (setting.dbType != dbTypeSqlite)
            setting.dbType = dbTypeSqlite
        }
        val urlChanged = setFieldValue(dbKeyUrl, setting?.dbUrl ?: "") { a -> setting.dbUrl = a }
        val userChanged = setFieldValue(dbKeyUser, setting?.dbUser ?: "") { a -> setting.dbUser = a }
        val passwordChanged = setFieldValue(dbKeyPassword, setting?.dbPassword ?: "") { a -> setting.dbPassword = a }
        if (urlChanged || userChanged || passwordChanged) {
            reconnectRequired = true
        }
        writeRequired =
            setFieldValue(encryptionSecretKey, setting?.encryptionSecret ?: "") { a -> setting.encryptionSecret = a }

        val driver = when (setting?.dbType ?: "") {
            dbTypeMySql -> "com.mysql.cj.jdbc.Driver"
            dbTypeEmbedded -> ""
            dbTypeSqlite -> ""
            dbTypePostgres -> "org.postgresql.Driver"
            else -> ""
        }
        if (driver != setting.dbDriver) {
            setting.dbDriver = driver
            reconnectRequired = true
        }

        return Pair(reconnectRequired, writeRequired)
    }

    private fun validateForm() {
        setting.validate()
    }

    private fun tryReconnect() : Boolean {
        try {
            val (result, error) = ApplicationData.testConnection(setting)
            if (!result) {
                lblError.text = "Error: there was a problem with the database connection ${error ?: message}"
                return false
            }
            /*
            MainScope().launch(Dispatchers.SWT) {
                val (result, error) = ApplicationData.testConnection(setting)
                if (!result) {
                    abortSave = true
                    lblError.display.asyncExec{
                        lblError.text = "Error: there was a problem with the database connection ${error ?: message}"
                    }
                }
            }
             */
        } catch (e: Exception) {
            ApplicationData.logError(e, "Error in Settings Dialog connecting to database ${setting}")
            lblError.text = "Error: there was a problem with the database connection ${e.message}"
            return false
            /*
            lblError.display.asyncExec{
                abortSave = true
                lblError.text = "Error: there was a problem with the database connection ${e.message}"
            }
             */
        }
        return true
    }

    override fun okPressed() {
        //val copy = setting.copy()
        lblError.text = ""
        var abortSave = false
        try {
            val (reconnectRequired, writeRequired) = transferFromForm()
            // validate before trying a connection
            validateForm()
            if (reconnectRequired) {
                abortSave = !tryReconnect()
                if(abortSave){
                    this.reconnect = false
                    return
                } else {
                    this.reconnect = true
                }
            }
            if (reconnectRequired || writeRequired) {
               setting.write()
            }
            super.okPressed()
        } catch (e: Exception) {
            ApplicationData.logError(e, "Error in Settings Dialog ${setting}")
            lblError.text = "Error, please check settings: ${e.message}"
        }
    }

    override fun createDialogArea(parent: Composite?): Control {
        val area = super.createDialogArea(parent) as Composite
        val container = Composite(area, SWT.NONE)
        container.layoutData = GridData(SWT.FILL, SWT.FILL, true, true)
        val layout = GridLayout(2, false)
        container.layout = layout

        radioMap = createDatabaseTypeGroup(container)
        widgetsMap = createFields(container)
        lblError = createErrorLabel(container)

        radioMap[dbTypeEmbedded]?.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                setEmbeddedOptions()
                setHelp(dbTypeEmbedded)
            }
        })

        radioMap[dbTypeSqlite]?.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                setNoAuthOptions()
                setHelp(dbTypeSqlite)
            }
        })

        radioMap[dbTypeMySql]?.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                setClientServerOptions()
                setHelp(dbTypeMySql)
            }
        })

        radioMap[dbTypePostgres]?.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                setClientServerOptions()
                setHelp(dbTypePostgres)
            }
        })

        return area
    }

    private fun setHelp(dbType: String) {
        val helpText = when (dbType) {
            dbTypeMySql -> "jdbc:mysql://[server]:3306/[database]?allowPublicKeyRetrieval=true&useSSL=false"
            dbTypeEmbedded -> "sqlite database stored in application"
            dbTypeSqlite -> "jdbc:sqlite:[path\\to\\file]"
            dbTypePostgres -> "jdbc:postgresql://[server]/[database]"
            else -> ""
        }
        lblAssistance.text = helpText
        widgetsMap[dbType]?.toolTipText = helpText
    }

    private fun setClientServerOptions() {
        widgetsMap[dbKeyUrl]?.enabled = true
        widgetsMap[dbKeyUser]?.enabled = true
        widgetsMap[dbKeyPassword]?.enabled = true
    }

    private fun setEmbeddedOptions() {
        widgetsMap[dbKeyUrl]?.enabled = false
        widgetsMap[dbKeyUser]?.enabled = false
        widgetsMap[dbKeyPassword]?.enabled = false
    }

    private fun setNoAuthOptions() {
        widgetsMap[dbKeyUrl]?.enabled = true
        widgetsMap[dbKeyUser]?.enabled = false
        widgetsMap[dbKeyPassword]?.enabled = false
    }


    private fun createDatabaseTypeGroup(parent: Composite): Map<String, Button> {
        val dbTypeGroup = Group(parent, SWT.NONE)
        dbTypeGroup.layout = RowLayout(SWT.VERTICAL);
        dbTypeGroup.text = "Database Type"

        val gdType = GridData()
        gdType.grabExcessHorizontalSpace = true
        gdType.horizontalAlignment = GridData.FILL
        gdType.horizontalSpan = 2

        val btnEmbedded = Button(dbTypeGroup, SWT.RADIO)
        btnEmbedded.text = "Embedded"
        btnEmbedded.selection = false

        val btnSqlite = Button(dbTypeGroup, SWT.RADIO)
        btnSqlite.text = "Sqlite"
        btnSqlite.selection = false

        val btnMySql = Button(dbTypeGroup, SWT.RADIO)
        btnMySql.text = "MySql"
        btnMySql.selection = false

        val btnPostGres = Button(dbTypeGroup, SWT.RADIO)
        btnPostGres.text = "PostGres"
        btnPostGres.selection = false

        dbTypeGroup.layoutData = gdType
        return mapOf(
            dbTypeEmbedded to btnEmbedded,
            dbTypeSqlite to btnSqlite,
            dbTypeMySql to btnMySql,
            dbTypePostgres to btnPostGres
        )
    }

    private fun createFields(parent: Composite): Map<String, Text> {

        val dbGroup = Group(parent, SWT.SHADOW_ETCHED_IN)
        dbGroup.text = "Database Properties"
        dbGroup.layout = GridLayout(2, false)

        val gd = GridData()
        gd.grabExcessHorizontalSpace = true
        gd.horizontalAlignment = GridData.FILL
        gd.horizontalSpan = 2
        dbGroup.layoutData = gd

        lblAssistance = createHelpLabel(dbGroup)
        val txtUrl = createField(dbGroup, "URL:")
        val txtUser = createField(dbGroup, "User:")
        val txtPassword = createField(dbGroup, "Password:")
        val txtSecret = createField(parent, "Encryption Secret:")
        return mapOf(
            dbKeyUrl to txtUrl,
            dbKeyUser to txtUser,
            dbKeyPassword to txtPassword,
            encryptionSecretKey to txtSecret
        )
    }

    private fun createField(parent: Composite, label: String): Text {
        val lbl = Label(parent, SWT.NONE)
        lbl.text = label
        val gdType = GridData()
        gdType.grabExcessHorizontalSpace = true
        gdType.horizontalAlignment = GridData.FILL
        val txt = Text(parent, SWT.BORDER)
        txt.layoutData = gdType
        return txt
    }

    private fun createErrorLabel(parent: Composite): Label {
        val lbl = Label(parent, SWT.WRAP)
        lbl.text = ""
        val gdType = GridData()
        gdType.grabExcessHorizontalSpace = true
        gdType.horizontalAlignment = GridData.FILL
        gdType.horizontalSpan = 2
        lbl.layoutData = gdType
        return lbl
    }

    private fun createHelpLabel(parent: Group): Label {
        val title = Label(parent, SWT.NONE)
        title.text = "Sample"
        val lbl = Label(parent, SWT.BORDER)
        lbl.text = ""
        val gdType = GridData()
        gdType.grabExcessHorizontalSpace = true
        gdType.horizontalAlignment = GridData.FILL
        lbl.layoutData = gdType
        return lbl
    }

    override fun configureShell(newShell: Shell?) {
        super.configureShell(newShell)
        newShell?.text = "Settings"
        newShell?.image = ImageUtils.getImage("preferences-system")
    }

    override fun getInitialSize(): Point = Point(640, 480)
}
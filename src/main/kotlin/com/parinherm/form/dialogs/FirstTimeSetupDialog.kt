/******************************
 * should this be replaced with the built in jface stuff
 * Creating an IRunnableWithProgress object with an IProgressMonitor instance to reflect progress
In the run method, calling beginTask at the start of the lengthy operation
Double each element of work, calling worked() on the monitor with an integer to reflect the number of units
just completed
Calling done() on the monitor when you're finished
 */

package com.parinherm.form.dialogs

import com.parinherm.ApplicationData
import com.parinherm.entity.AppVersion
import com.parinherm.entity.schema.AppVersionMapper
import com.parinherm.entity.schema.SchemaBuilder
import com.parinherm.font.FontUtils
import com.parinherm.image.ImageUtils
import com.parinherm.lookups.LookupUtils
import com.parinherm.server.DefaultViewDefinitions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.dialogs.IDialogConstants
import org.eclipse.jface.dialogs.IMessageProvider
import org.eclipse.jface.dialogs.TitleAreaDialog
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.jface.layout.GridLayoutFactory
import org.eclipse.jface.viewers.ArrayContentProvider
import org.eclipse.jface.viewers.ListViewer
import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.*

class FirstTimeSetupDialog(parentShell: Shell?) : TitleAreaDialog(parentShell) {
    private val dataList = WritableList<String>()
    private val data = listOf(
        "starting..."
    )
    private lateinit var label: Label
    private lateinit var listView: ListViewer

    private fun addNotification(message: String){
        /* this should update the ui on the ui thread */
        listView.control.display.asyncExec {
            dataList.add(message)
            if(!listView.list.isDisposed){
                listView.refresh()
            }
        }
    }

    private suspend fun runSetup(){
        val completeMessage = "complete."
        addNotification("building tables...")
        SchemaBuilder.build()
        addNotification(completeMessage)
        val appVersion = AppVersion(0, ApplicationData.version)
        addNotification("set version...")
        AppVersionMapper.save(appVersion)
        addNotification(completeMessage)
        addNotification("insert default data...")
        addNotification("inserting lookups, may take some time...")
        LookupUtils.createDefaults()
        addNotification("inserting default views, may take some time...")
        DefaultViewDefinitions.makeDefaultViews()
        addNotification(completeMessage)

        addNotification("first time setup is complete!")
        //LookupMapper.encryptAllRows("password_master")
        //LoginMapper.encryptAll()
        val button = getButton(IDialogConstants.OK_ID)
        if(!button.isDisposed){
            button.display.asyncExec {
                label.text = "First Time Setup on database complete."
                getButton(IDialogConstants.OK_ID).enabled = true
            }
        }
    }


    override fun create() {
        super.create()
        val message = "${ApplicationData.VENDOR_NAME} C.O.U.P Initial Setup"
        shell.text = message
        setTitle("Application Initial Setup")
        setMessage("running first time setup on database", IMessageProvider.INFORMATION)
        setTitleImage(ImageUtils.getImage(ImageUtils.IMAGE_LOGO))
        this.shell.font = FontUtils.getFont(FontUtils.FONT_IBM_PLEX_MONO_HEADER)
        getButton(IDialogConstants.OK_ID).enabled = false
        MainScope().launch(Dispatchers.IO) {
            runSetup()
        }
    }

    override fun isResizable(): Boolean {
        return false
    }

    override fun createDialogArea(parent: Composite?): Control {
        val content = Composite(super.createDialogArea(parent) as Composite, ApplicationData.swnone)
        label = Label(content, SWT.NONE)
        label.text = "First Time Setup on database, please wait."
        val listContainer = Composite(content, ApplicationData.swnone)
        listView = ListViewer(listContainer, ApplicationData.swnone)
        val contentProvider = ArrayContentProvider.getInstance()
        listView.contentProvider = contentProvider
        listView.labelProvider = StringLabelProvider()
        dataList.addAll(data)
        listView.input = dataList
        listContainer.layout = FillLayout()

        GridDataFactory.fillDefaults().grab(true, false).applyTo(label)
        GridDataFactory.fillDefaults().grab(true, true).applyTo(listContainer)

        GridLayoutFactory.fillDefaults().numColumns(1).generateLayout(content)
        GridDataFactory.fillDefaults().grab(true, true).applyTo(content)
        return content
    }

    override fun getInitialSize(): Point {
        return Point(640, 480)
    }

    override fun createButtonsForButtonBar(parent: Composite?) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }
}
package com.parinherm.viewmodel

import com.oracle.truffle.js.parser.env.Environment
import com.parinherm.ApplicationData
import com.parinherm.audio.AudioClient
import com.parinherm.audio.SpeechRecognition
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.SalesforceConfig
import com.parinherm.entity.schema.SalesforceConfigMapper
import com.parinherm.form.FormViewModel
import com.parinherm.integration.SalesforceRest
import com.parinherm.lookups.LookupUtils
import com.parinherm.menus.TabInfo
import com.parinherm.view.SalesforceConfigView
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Text

class SalesforceConfigViewModel(tabInfo: TabInfo) : FormViewModel<SalesforceConfig>(
    SalesforceConfigView(tabInfo.folder, Comparator()),
    SalesforceConfigMapper,
    { SalesforceConfig(0L, "", "", "") },
    tabInfo
) {

    private final val DATA_URL = "/services/data"
    private final val SOBJECTS_URL = "/services/data/v32.0/sobjects"
    private final val commandStack: ArrayDeque<String> = ArrayDeque()

    init {
        val textWidget = view.form.formWidgets["output"]?.widget as Text
        createTab()
        loadData(mapOf())

        //do as a stack of commands
        //recognize is between key values
        //commit ends the steram
        try {
            AudioClient.processingFunction = { command: String, audio: ByteArray, length: Int ->
                if(command.isNotEmpty()){
                    val baseUrl = "${currentEntity!!.salesforceUrl}"
                    if(command.lowercase().equals("execute")){
                        val loginToken = currentEntity!!.loginToken
                        textWidget.text = "Please wait executing command"
                        val result = SalesforceRest.parseCommand(loginToken, baseUrl, commandStack)
                        textWidget.text += System.lineSeparator()
                        textWidget.text += "Result was: ${result.toString()}"
                        commandStack.clear()
                    } else if(command.lowercase().equals("clear")){
                        textWidget.text = "cleared commands"
                        commandStack.clear()
                    } else if (command.lowercase() == "authorize"){
                        SalesforceRest.generateNewAccessToken(baseUrl)
                    }
                    else {
                        commandStack.addLast(command)
                        textWidget.text += System.lineSeparator()
                        textWidget.text += command
                    }

                    /*
                    if(command.lowercase() == "create lead"){
                        textWidget.text = "create lead"
                    }
                     */
                }
            }
            AudioClient.open()
            SpeechRecognition.open()
        } catch (e: Exception) {
            ApplicationData.logError(e, "Error opening Audio Client")
        }


        /*
        val salesForceConfigView = view as SalesforceConfigView
        salesForceConfigView.testButton.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                if (currentEntity != null) {
                    Display.getDefault().asyncExec {
                        try {
                            Display.getDefault().activeShell.cursor =
                                Display.getDefault().getSystemCursor(SWT.CURSOR_WAIT)
                            //val testUrl = "${currentEntity!!.salesforceUrl}${SOBJECTS_URL}"
                            val testUrl = "${currentEntity!!.salesforceUrl}${view.commandInput.text}"
                            val result = SalesforceRest.test(testUrl, currentEntity!!.loginToken)
                            textWidget.text = result
                            view.commandOutput.text = testUrl
                        } catch (e: Exception) {
                            textWidget.text = e.message
                            Display.getDefault().timerExec(200) {}
                        } finally {
                            Display.getDefault().activeShell.cursor = null
                        }
                    }
                }
            }
        })


        salesForceConfigView.createLeadButton.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                if (currentEntity != null) {
                    Display.getDefault().asyncExec {
                        try {
                            //Display.getDefault().activeShell.cursor = Display.getDefault().getSystemCursor(SWT.CURSOR_WAIT)
                            val testUrl = "${currentEntity!!.salesforceUrl}/services/data/v32.0/sobjects/Lead/"
                            SalesforceRest.createLead(
                                testUrl,
                                currentEntity!!.loginToken,
                                "Med",
                                "Adhoc",
                                "Testing Rest",
                                "1234567890"
                                )
                            view.commandOutput.text = testUrl
                        } catch (e: Exception) {
                            textWidget.text = e.message
                            Display.getDefault().timerExec(200) {}
                        } finally {
                            Display.getDefault().activeShell.cursor = null
                        }
                    }
                }
            }
        })
         */
    }

    class Comparator : BeansViewerComparator(), IViewerComparator {

        val name_index = 0
        val salesforceUrl_index = 1
        val loginToken_index = 2

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as SalesforceConfig
            val entity2 = e2 as SalesforceConfig
            val rc = when (propertyIndex) {
                name_index -> compareString(entity1.name, entity2.name)
                salesforceUrl_index -> entity1.salesforceUrl.compareTo(entity2.salesforceUrl)
                loginToken_index -> entity1.loginToken.compareTo(entity2.loginToken)
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }

}

package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.entity.schema.LoginMapper
import com.parinherm.entity.schema.LookupMapper
import com.parinherm.font.FontUtils
import com.parinherm.form.widgets.createSection
import com.parinherm.form.widgets.createSectionContents
import com.parinherm.form.widgets.createSectionHyperlink
import com.parinherm.image.ImageUtils
import com.parinherm.menus.TabInfo
import com.parinherm.menus.recipeRunner
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Composite
import org.eclipse.ui.forms.events.HyperlinkAdapter
import org.eclipse.ui.forms.events.HyperlinkEvent
import org.eclipse.ui.forms.widgets.FormToolkit
import org.eclipse.ui.forms.widgets.TableWrapLayout

class RecryptView (parent: Composite) : Composite(parent, ApplicationData.swnone) {

    val toolkit = FormToolkit(parent.display)
    val form = toolkit.createScrolledForm(this)
    val wrapLayout = TableWrapLayout()

    init {
        try {
            form.text = "Re-Encrypt System Data"
            form.font = FontUtils.getFont(FontUtils.FONT_IBM_PLEX_MONO_HEADER)
            form.image = ImageUtils.getImage(ImageUtils.IMAGE_SYSTEM_INSTALLER)
            makeSection()
            wrapLayout.numColumns = 1
            form.body.layout = wrapLayout
            this.layout = FillLayout(SWT.VERTICAL)
            this.layout()
        } catch (e: Exception){
            ApplicationData.logError(e, "Error initializing RecryptView")
        }

    }

    private fun makeSection() {
        val description = """
            Run re-encryption after changing the encryption secret in settings. 
        """.trimIndent()
        val section = createSection(toolkit, form, "Run re-encryption", description)
        val contents = createSectionContents(toolkit, section)
        val button = toolkit.createButton(contents, "&Run", SWT.PUSH)
        button.image = ImageUtils.getImage(ImageUtils.IMAGE_APPLICATION_X_EXECUTABLE)
        button.addSelectionListener(object: SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                button.enabled = false
                LookupMapper.encryptAllRows("password_master")
                LoginMapper.encryptAll()
                button.enabled = true
            }
        })

        /*
        val recipes = createSectionHyperlink(toolkit, contents, "Recipes", ImageUtils.IMAGE_FACE_GRIN)
        recipes.addHyperlinkListener(object: HyperlinkAdapter(){
            override fun linkActivated(e: HyperlinkEvent?) {
                recipeRunner.invoke(
                    TabInfo(ApplicationData.mainWindow.folder,
                    "Recipes",
                    FontUtils.FONT_IBM_PLEX_MONO,
                    ImageUtils.IMAGE_FACE_GRIN)
                )
            }
        })
         */
        section.client = contents
    }


}
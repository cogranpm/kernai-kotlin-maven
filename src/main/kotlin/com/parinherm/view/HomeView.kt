package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.font.FontUtils
import com.parinherm.form.widgets.createBody
import com.parinherm.form.widgets.createSection
import com.parinherm.form.widgets.createSectionContents
import com.parinherm.form.widgets.createSectionHyperlink
import com.parinherm.image.ImageUtils
import com.parinherm.menus.*
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Composite
import org.eclipse.ui.forms.events.HyperlinkAdapter
import org.eclipse.ui.forms.events.HyperlinkEvent
import org.eclipse.ui.forms.widgets.*

class HomeView(parent: Composite) : Composite(parent, ApplicationData.swnone){

    val toolkit = FormToolkit(parent.display)
    val form = toolkit.createScrolledForm(this)
    val wrapLayout = TableWrapLayout()

    init {
        form.text = "${ApplicationData.APPLICATION_DISPLAY_NAME}"
        form.font = FontUtils.getFont(FontUtils.FONT_IBM_PLEX_MONO_BIG)
        form.image = ImageUtils.getImage(ImageUtils.IMAGE_LOGO)

        makeSecuritySection()
        makeDeveloperSection()
        makeSystemSection()
        makeMiscSection()

        wrapLayout.numColumns = 2

        form.body.layout = wrapLayout
        this.layout = FillLayout(SWT.VERTICAL)
        this.layout()
    }

    private fun makeMiscSection() {
        val description = """
            Household utilities. 
        """.trimIndent()
        val section = createSection(toolkit, form, "Household", description)
        val contents = createSectionContents(toolkit, section)
        val recipes = createSectionHyperlink(toolkit, contents, "Recipes", ImageUtils.IMAGE_GENERIC_DOCUMENT)
        recipes.addHyperlinkListener(object: HyperlinkAdapter(){
            override fun linkActivated(e: HyperlinkEvent?) {
                recipeRunner.invoke(TabInfo(ApplicationData.mainWindow.folder,
                    "Recipes",
                    FontUtils.FONT_IBM_PLEX_MONO,
                    ImageUtils.IMAGE_GENERIC_DOCUMENT))
            }
        })
        section.client = contents
    }

    private fun makeSecuritySection(){

        /*
        val securityDescription = """
           <form>
           <p>
           The security features allow you to store all your Login and Password information encrypted in the local database. 
           It gives you a central place to securely store this information.
           First you would set up all the master passwords that you use on the various platforms and sites.
           For example you might use the same password for all your shopping accounts, but a different password for your work login.
           Accordingly you would set up each of these different passwords via the Passwords link below.
           Next use the Logins link to enter information for each place you need to login. For example you would set up a different login for your 
           trading account, shopping account, each of your computer accounts and so on.
           In the Logins form enter your user name, and pick the password from one of the passwords entered on the Passwords form.
           You can also add a URL, notes, and give the Login a name, for example "Home Computer Network" or "Home WIFI"
           Note: if you need to change a password, it is recommended to add a new password via the Passwords form, then also update the relevant Login form.
           </p>
           </form>
        """.trimIndent()
         */
        val securitySection = createSection(toolkit, form, "Security - Passwords and Logins", "Manage Logins and Passwords")
        val securityContents = createSectionContents(toolkit, securitySection)
        //createBody(toolkit, securityContents, securityDescription)
        val passwordLink = createSectionHyperlink(toolkit, securityContents, "Passwords", ImageUtils.IMAGE_SYSTEM_LOCK_SCREEN)
        val loginsLink = createSectionHyperlink(toolkit, securityContents, "Logins", ImageUtils.IMAGE_APPLICATIONS_INTERNET)
        passwordLink.addHyperlinkListener(object: HyperlinkAdapter(){
            override fun linkActivated(e: HyperlinkEvent?) {
                passwordRunner.invoke(TabInfo(ApplicationData.mainWindow.folder,
                    "Password",
                    FontUtils.FONT_IBM_PLEX_MONO,
                    ImageUtils.IMAGE_SYSTEM_LOCK_SCREEN))
            }
        })
        loginsLink.addHyperlinkListener(object: HyperlinkAdapter(){
            override fun linkActivated(e: HyperlinkEvent?) {
                loginRunner.invoke(TabInfo(ApplicationData.mainWindow.folder,
                    "Login",
                    FontUtils.FONT_IBM_PLEX_MONO,
                    ImageUtils.IMAGE_APPLICATIONS_INTERNET))
            }
        })
        securitySection.client = securityContents

    }

    private fun makeDeveloperSection(){
        /*
        val description = """
           The tools in this section assist the developer to organise their accumulated knowledge.
           The Snippets form is to try out source code or record useful snippets for future reference.
           Perhaps there is snippet of code from Stack Overflow you don't want to forget. Enter it here to retain and categorize
           For the Kotlin and Javascript languages you can execute the code directly from the form using the execute button.
           The Notebooks form is a simple way to store some notes, with a structure suited to development content.
           The Shelf form is designed to mirror your technical library and has tools to assist in the retention of knowledge.
        """.trimIndent()
         */

        val developerSection = createSection(toolkit, form, "Developers", "Tools for developers")
        val developerContents = createSectionContents(toolkit, developerSection)
        val snippetsLink = createSectionHyperlink(toolkit, developerContents, "Snippets", ImageUtils.IMAGE_APPLICATION_X_EXECUTABLE)
        val noteBooksLink = createSectionHyperlink(toolkit, developerContents, "Notebooks", ImageUtils.IMAGE_X_OFFICE_DOCUMENT)
        val shelfLink  = createSectionHyperlink(toolkit, developerContents, "Bookshelf", ImageUtils.IMAGE_GENERIC_DOCUMENT)
        val salesForceLink = createSectionHyperlink(toolkit, developerContents, "Salesforce", ImageUtils.IMAGE_SYSTEM_USERS)
        snippetsLink.addHyperlinkListener(object: HyperlinkAdapter(){
            override fun linkActivated(e: HyperlinkEvent?) {
                snippetsRunner.invoke(TabInfo(ApplicationData.mainWindow.folder,
                    "Snippets",
                    FontUtils.FONT_IBM_PLEX_MONO,
                    ImageUtils.IMAGE_APPLICATION_X_EXECUTABLE))
            }
        })
        noteBooksLink.addHyperlinkListener(object: HyperlinkAdapter(){
            override fun linkActivated(e: HyperlinkEvent?) {
                noteBookRunner.invoke(TabInfo(ApplicationData.mainWindow.folder,
                    "Notebooks",
                    FontUtils.FONT_IBM_PLEX_MONO,
                    ImageUtils.IMAGE_X_OFFICE_DOCUMENT))
            }
        })
        shelfLink.addHyperlinkListener(object: HyperlinkAdapter(){
            override fun linkActivated(e: HyperlinkEvent?) {
                shelfRunner.invoke(TabInfo(ApplicationData.mainWindow.folder,
                    "Shelf",
                    FontUtils.FONT_IBM_PLEX_MONO,
                    ImageUtils.IMAGE_SYSTEM_FILE_MANAGER))
            }
        })
        salesForceLink.addHyperlinkListener(object: HyperlinkAdapter(){
            override fun linkActivated(e: HyperlinkEvent?) {
                salesforceRunner.invoke(TabInfo(ApplicationData.mainWindow.folder,
                    "Salesforce",
                    FontUtils.FONT_IBM_PLEX_MONO,
                    ImageUtils.IMAGE_SYSTEM_USERS))
            }
        })
        developerSection.client =developerContents

    }

    private fun makeSystemSection(){
       val description = """
            The tools in this section allow you to make your own forms. You can also adjust some aspects of the built in forms. 
        """.trimIndent()
        val section = createSection(toolkit, form, "Build your own Forms", description)
        val contents = createSectionContents(toolkit, section)
        val formsLink = createSectionHyperlink(toolkit, contents, "Forms", ImageUtils.IMAGE_FORMAT_JUSTIFY_FILL)
        val associationsLink = createSectionHyperlink(toolkit, contents, "Associations", ImageUtils.IMAGE_APPLICATIONS_INTERNET)
        val lookupsLink = createSectionHyperlink(toolkit, contents, "Lookups", ImageUtils.IMAGE_EDIT_FIND)
        val menusLink = createSectionHyperlink(toolkit, contents, "Menus", ImageUtils.IMAGE_PREFERENCES_DESKTOP)
        val settingsLink = createSectionHyperlink(toolkit, contents, "Settings", ImageUtils.IMAGE_PREFERENCES_SYSTEM)

        formsLink.addHyperlinkListener(object: HyperlinkAdapter(){
            override fun linkActivated(e: HyperlinkEvent?) {
                viewDefRunner.invoke(TabInfo(ApplicationData.mainWindow.folder,
                    "Forms",
                    FontUtils.FONT_IBM_PLEX_MONO,
                    ImageUtils.IMAGE_APPLICATION_X_EXECUTABLE))
            }
        })

       associationsLink.addHyperlinkListener(object: HyperlinkAdapter(){
           override fun linkActivated(e: HyperlinkEvent?) {
               associationDefRunner.invoke(TabInfo(ApplicationData.mainWindow.folder,
                   "Associations",
                   FontUtils.FONT_IBM_PLEX_MONO,
                   ImageUtils.IMAGE_APPLICATIONS_INTERNET
                   ))
           }
       })

        lookupsLink.addHyperlinkListener(object: HyperlinkAdapter(){
            override fun linkActivated(e: HyperlinkEvent?) {
                lookupsRunner.invoke(TabInfo(ApplicationData.mainWindow.folder,
                    "Lookups",
                    FontUtils.FONT_IBM_PLEX_MONO,
                    ImageUtils.IMAGE_FORMAT_JUSTIFY_FILL))
            }
        })
        menusLink.addHyperlinkListener(object: HyperlinkAdapter(){
            override fun linkActivated(e: HyperlinkEvent?) {
                menusRunner.invoke(TabInfo(ApplicationData.mainWindow.folder,
                    "Menus",
                    FontUtils.FONT_IBM_PLEX_MONO,
                    ImageUtils.IMAGE_PREFERENCES_DESKTOP))
            }
        })
        settingsLink.addHyperlinkListener(object: HyperlinkAdapter(){
            override fun linkActivated(e: HyperlinkEvent?) {
                settingsRunner.invoke(TabInfo(ApplicationData.mainWindow.folder,
                    "Settings",
                    FontUtils.FONT_IBM_PLEX_MONO,
                    ImageUtils.IMAGE_PREFERENCES_SYSTEM))
            }
        })
        section.client = contents
    }


    override fun setFocus(): Boolean {
        form.setFocus()
        return super.setFocus()
    }

    override fun dispose() {
        toolkit.dispose()
        super.dispose()
    }
}
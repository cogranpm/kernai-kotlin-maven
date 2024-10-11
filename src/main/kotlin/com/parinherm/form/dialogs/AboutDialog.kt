/* necessary attributions
Tango Desktop Project -- for the icons
 */

package com.parinherm.form.dialogs

import com.parinherm.ApplicationData
import com.parinherm.font.FontUtils
import com.parinherm.form.getListViewer
import com.parinherm.form.makeColumns
import com.parinherm.form.makeViewerLabelProvider
import com.parinherm.image.ImageUtils
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider
import org.eclipse.jface.dialogs.IDialogConstants
import org.eclipse.jface.dialogs.IMessageProvider
import org.eclipse.jface.dialogs.TitleAreaDialog
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.jface.layout.GridLayoutFactory
import org.eclipse.jface.layout.TableColumnLayout
import org.eclipse.jface.viewers.ArrayContentProvider
import org.eclipse.jface.viewers.LabelProvider
import org.eclipse.jface.viewers.ListViewer
import org.eclipse.jface.viewers.TableViewer
import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Control
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Shell

class StringLabelProvider: LabelProvider() {
    override fun getText(element: Any?): String {
        return element.toString()
    }
}

class AboutDialog  (parentShell: Shell) : TitleAreaDialog(parentShell) {

    private val dataList = WritableList<String>()
    private val data = listOf(
        "JetBrains Kotlin Progamming Language (https://kotlinlang.org/)",
        "JetBrains Exposed (https://github.com/JetBrains/Exposed)",
        "Kotlin Couroutines SWT (https://github.com/brudaswen/kotlinx-coroutines-swt)",
        "Tango Desktop Project (http://tango.freedesktop.org/Tango_Desktop_Project)",
        "Java Simplified Encryption (http://www.jasypt.org/)",
        "Eclipse Standard Widget Toolkit (https://www.eclipse.org/swt/)",
        "Handlebars Java (https://github.com/jknack/handlebars.java)",
        "GraalVM (https://www.graalvm.org/)",
        "MySQL Connector Java (https://www.mysql.com/products/connector/)",
        "Sqlite JDBC (https://github.com/xerial/sqlite-jdbc)",
        "PostgreSQL JDBC Driver (https://jdbc.postgresql.org/)",
        "ClassGraph (https://github.com/classgraph/classgraph)",
        "IBM Plex Mono Font (https://fonts.google.com/specimen/IBM+Plex+Mono)"
    )
    private lateinit var listView: ListViewer

    override fun create() {
        super.create()
        shell.text = "About Parinherm C.O.U.P"
        setTitle("Parinherm C.O.U.P")
        setMessage("${ApplicationData.APPLICATION_DISPLAY_NAME}.", IMessageProvider.INFORMATION)
        setTitleImage(ImageUtils.getImage(ImageUtils.IMAGE_LOGO))
        this.shell.font = FontUtils.getFont(FontUtils.FONT_IBM_PLEX_MONO_HEADER)
    }

    override fun isResizable(): Boolean {
        return false
    }


    override fun createDialogArea(parent: Composite?): Control {
        val content = Composite(super.createDialogArea(parent) as Composite, ApplicationData.swnone)
        val label = Label(content, SWT.NONE)
        label.text = "Powered by open source software:"
        val listContainer = Composite(content, ApplicationData.swnone)
        listView = ListViewer(listContainer, ApplicationData.swnone)
        val contentProvider = ArrayContentProvider.getInstance()
        listView.contentProvider = contentProvider
        listView.labelProvider = StringLabelProvider()
        dataList.addAll(data)
        listView.input = dataList

        listContainer.layout = FillLayout()
        //GridLayoutFactory.fillDefaults().numColumns(1).generateLayout(listContainer)

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
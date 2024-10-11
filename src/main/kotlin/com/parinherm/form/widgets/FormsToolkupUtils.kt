package com.parinherm.form.widgets

import com.parinherm.ApplicationData
import com.parinherm.image.ImageUtils
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite
import org.eclipse.ui.forms.widgets.*

fun createSection(toolkit: FormToolkit, form: ScrolledForm, text: String, description: String): Section{
    val section = toolkit.createSection(form.body, Section.DESCRIPTION or Section.TWISTIE or Section.EXPANDED)
    section.text = text
    toolkit.createCompositeSeparator(section)
    section.description = description
    section.layoutData = TableWrapData(TableWrapData.FILL_GRAB)
    return section
}

fun createSectionContents(toolkit: FormToolkit, section: Section) : Composite{
    val contents = toolkit.createComposite(section, SWT.WRAP)
    contents.layout = TableWrapLayout()
    contents.layoutData = TableWrapData(TableWrapData.FILL_GRAB)
    return contents
}

fun createBody(toolkit: FormToolkit, container: Composite, body: String){
    val text = toolkit.createText(container, body, SWT.WRAP or SWT.NONE)
    text.layoutData = TableWrapData(TableWrapData.LEFT)
}

fun createSectionHyperlink(toolkit: FormToolkit, container: Composite, text:String, imageKey: String?): ImageHyperlink {
    val hyperLink = toolkit.createImageHyperlink(container, SWT.WRAP)
    hyperLink.text = text
    if(imageKey != null){
        hyperLink.image = ImageUtils.getImage(imageKey)
    }
    hyperLink.layoutData = TableWrapData(TableWrapData.FILL)
    return hyperLink
}

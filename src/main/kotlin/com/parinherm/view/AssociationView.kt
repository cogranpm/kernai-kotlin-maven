package com.parinherm.view

import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.AssociationDefinition
import com.parinherm.form.Form
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.form.makeHeaderText
import com.parinherm.image.ImageUtils
import com.parinherm.server.DefaultViewDefinitions
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.swt.widgets.Composite

class AssociationView(val parent: Composite, comparator: BeansViewerComparator): View<AssociationDefinition> {
    override val form: Form<AssociationDefinition> = Form(parent,
        DefaultViewDefinitions.loadView(ViewDefConstants.associationViewId),
        comparator
    )
    val editToolbar = form.toolbar

    init {
        //makeHeaderText(form.headerSection, "Enter snippets of code in your programming language. Execute the code using the run button. Only kotlin or javascript can be executed at this time.")
    }

}
package com.parinherm.view

import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.ViewDefinition
import com.parinherm.form.Form
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.server.DefaultViewDefinitions
import org.eclipse.swt.widgets.Composite

class ChildViewDefinitionView(parent: Composite, comparator: BeansViewerComparator) : View <ViewDefinition> {

    override val form: Form<ViewDefinition> = Form(parent,
        DefaultViewDefinitions.loadView(ViewDefConstants.viewDefinitionViewId),
        comparator
    )
}
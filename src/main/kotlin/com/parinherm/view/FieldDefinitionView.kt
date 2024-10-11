package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.FieldDefinition
import com.parinherm.form.Form
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.server.DefaultViewDefinitions
import org.eclipse.swt.widgets.Composite

class FieldDefinitionView(val parent: Composite, comparator: BeansViewerComparator)
    : View <FieldDefinition> {

        override val form: Form<FieldDefinition> = Form(parent,
            DefaultViewDefinitions.loadView(ViewDefConstants.fieldDefinitionViewId),
            comparator
        )

}
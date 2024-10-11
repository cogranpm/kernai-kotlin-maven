package com.parinherm.view

import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.LookupDetail
import com.parinherm.form.Form
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.server.DefaultViewDefinitions
import org.eclipse.swt.widgets.Composite

class PasswordView(val parent: Composite, comparator: BeansViewerComparator)
    : View <LookupDetail> {

        override val form: Form<LookupDetail> = Form(parent,
            DefaultViewDefinitions.loadView(ViewDefConstants.passwordsViewId),
            comparator
        )

}

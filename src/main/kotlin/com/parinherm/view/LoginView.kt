package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Login
import com.parinherm.form.Form
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.server.DefaultViewDefinitions
import com.parinherm.view.filter.LoginViewFilter
import org.eclipse.swt.widgets.Composite

class LoginView (val parent: Composite, comparator: BeansViewerComparator) : View<Login> {

    /* load view from data now instead of the json file based approach */
    override val form: Form<Login> = Form(parent,
        DefaultViewDefinitions.loadView(ViewDefConstants.loginViewId),
        comparator,
        LoginViewFilter()
    )

}
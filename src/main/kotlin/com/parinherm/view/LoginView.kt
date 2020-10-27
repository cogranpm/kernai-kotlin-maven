package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Login
import com.parinherm.form.Form
import org.eclipse.swt.widgets.Composite

class LoginView (val parent: Composite, comparator: BeansViewerComparator) : View<Login> {

    // this member has all of the widgets
    // it's a common object favour composition over inheritance
    override val form: Form<Login> = Form(parent, ApplicationData.getView(ApplicationData.ViewDefConstants.loginViewId), comparator)

}
package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Menu
import com.parinherm.form.Form
import com.parinherm.server.DefaultViewDefinitions
import com.parinherm.view.filter.RecipeViewFilter
import org.eclipse.swt.widgets.Composite

class MenuView(val parent: Composite, comparator: BeansViewerComparator)
    : View <Menu> {

        override val form: Form<Menu> = Form(parent,
            DefaultViewDefinitions.loadView("menumanager"),
            comparator
        )

}

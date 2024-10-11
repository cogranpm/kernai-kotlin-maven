package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.MenuItem
import com.parinherm.form.Form
import com.parinherm.server.DefaultViewDefinitions
import com.parinherm.view.filter.RecipeViewFilter
import org.eclipse.swt.widgets.Composite

class MenuItemView(val parent: Composite, comparator: BeansViewerComparator)
    : View <MenuItem> {

        override val form: Form<MenuItem> = Form(parent,
            DefaultViewDefinitions.loadView("menuitem"),
            comparator
        )

}

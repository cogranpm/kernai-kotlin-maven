package com.parinherm.viewmodel

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.builders.IViewerComparator
import com.parinherm.entity.Login
import com.parinherm.entity.schema.LoginMapper
import com.parinherm.form.FormViewModel
import com.parinherm.view.LoginView
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.custom.CTabFolder

class LoginViewModel (parent: CTabFolder)  : FormViewModel<Login>(
    LoginView(parent, Comparator()),
    LoginMapper, { Login.make() }) {

    init {
        loadData(mapOf())
    }

    class Comparator : BeansViewerComparator(), IViewerComparator {

        val name_index = 0
        val category_index = 1
        val userName_index = 2
        val password_index = 3
        val url_index = 4

        override fun compare(viewer: Viewer?, e1: Any?, e2: Any?): Int {
            val entity1 = e1 as Login
            val entity2 = e2 as Login
            val rc = when (propertyIndex) {
                name_index -> compareString(entity1.name, entity2.name)
                category_index -> compareLookups(entity1.category, entity2.category, ApplicationData.loginCategoryList)
                userName_index -> compareString(entity1.userName, entity2.userName)
                password_index -> compareLookups(entity1.password, entity2.password, ApplicationData.passwordMaster)
                url_index -> compareString(entity1.url, entity2.url)
                else -> 0
            }
            return flipSortDirection(rc)
        }
    }

}
package com.parinherm.server

import com.google.gson.GsonBuilder
import com.parinherm.ApplicationData.ViewDef

object ViewBuilder {

    fun makeDefinitions(): Map<String, Any>  {
        // definition of data is something like a view is:
        // a map of properties needed by view
        // a list of maps per view that represents the fields on a form etc etc
        val firstNameDef = mapOf(
            ViewDef.fieldName to "FirstName",
            ViewDef.title to "First Name",
            ViewDef.fieldDataType to ViewDef.text)

        val lastNameDef = mapOf(
            ViewDef.fieldName to "LastName",
            ViewDef.title to "Last Name",
            ViewDef.fieldDataType to ViewDef.text)

        val bindingTestDef = mapOf(
            ViewDef.viewid to ViewDef.bindingTestViewId,
            ViewDef.title to "Binding Test",
            ViewDef.fields to listOf(firstNameDef, lastNameDef))


        val viewDefinitions = mapOf(
            ViewDef.version to 1,
            ViewDef.forms to listOf(bindingTestDef)
        )

        return viewDefinitions
    }
}
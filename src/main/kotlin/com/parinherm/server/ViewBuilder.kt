package com.parinherm.server

import com.google.gson.GsonBuilder
import com.parinherm.ApplicationData.ViewDef

object ViewBuilder {

    fun makeDefinitions(): String {
        //load these from server or somewhere
        // definition of data is something like a view is:
        // a map of properties needed by view
        // a list of maps per view that represents the fields on a form
        val firstNameDef = mapOf(
            ViewDef.title to "First Name",
            ViewDef.inputType to ViewDef.text)

        val lastNameDef = mapOf(
            ViewDef.title to "Last Name",
            ViewDef.inputType to ViewDef.text)

        val bindingTestDef = mapOf(
            ViewDef.title to "Kernai",
            ViewDef.fields to listOf(firstNameDef, lastNameDef))


        val viewDefinitions = mapOf(
            ViewDef.version to 1,
            ViewDef.forms to listOf(bindingTestDef)
        )

        //transform to json for wire format
        val gson = GsonBuilder().create()
        return gson.toJson(viewDefinitions)
    }
}
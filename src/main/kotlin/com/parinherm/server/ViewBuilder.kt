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
            ViewDef.props["title"] to "First Name",
            "inputType" to "text")

        val lastNameDef = mapOf(
            "title" to "Last Name",
            "inputType" to "text")

        val bindingTestDef = mapOf(
            "title" to "kernai",
            "type" to 1, "version" to 1,
            "fields" to listOf(firstNameDef, lastNameDef))


        val viewDefinitions = mapOf(
            "version" to 1,
            "forms" to listOf(bindingTestDef)
        )

        //transform to json for wire format
        val gson = GsonBuilder().create()
        return gson.toJson(viewDefinitions)
    }
}
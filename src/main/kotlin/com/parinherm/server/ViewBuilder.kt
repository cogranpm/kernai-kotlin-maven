package com.parinherm.server

import com.google.gson.GsonBuilder

object ViewBuilder {

    fun getViewDefinitions(): String {
        //load these from server or somewhere
        // definition of data is something like a view is:
        // a map of properties needed by view
        // a list of maps per view that represents the fields on a form
        val firstNameDef = mapOf("title" to "First Name", "includeLabel" to false)
        val lastNameDef = mapOf("title" to "Last Name", "includeLabel" to true)
        val formDef = mapOf("title" to "kernai", "type" to 1,
            "fields" to listOf(firstNameDef, lastNameDef))

        //transform to json for wire format
        val gson = GsonBuilder().create()
        return gson.toJson(formDef)
    }
}
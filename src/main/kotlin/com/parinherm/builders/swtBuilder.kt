package com.parinherm.builders
/* loads application definition from server
and is able to build a ui from it
 */

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.builtins.list

@Serializable
data class WindowDef(val title: String, val height: Int, val fields: List<FieldDef>)

@Serializable
data class FieldDef(val title: String, val type: Int)

object swtBuilder {

    @ImplicitReflectionSerializer
    fun renderTest(){

        val json = Json(JsonConfiguration.Stable)

        val dataBindingFields = listOf<FieldDef>(
            FieldDef("FirstName", 0),
            FieldDef("LastName", 0))

        val testFields = listOf<FieldDef>(
            FieldDef("Summary", 1),
            FieldDef("mosculum", 4))

        val listStuff = listOf(
            WindowDef("Kernai", 100, dataBindingFields),
            WindowDef("Test", 100, testFields))

        val jsonData = json.stringify(WindowDef.serializer().list, listStuff)
        println(jsonData)
        val rehydryatedData = json.parse(WindowDef.serializer().list, jsonData)
        rehydryatedData.forEach {
            println("${it.title}")
            it.fields.forEach {
                println("${it.title}")
            }
        }
    }
}
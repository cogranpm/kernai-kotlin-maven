package com.parinherm.builders
/* loads application definition from server
and is able to build a ui from it
 */

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*
import kotlinx.serialization.builtins.list

@Serializable
data class WindowDef(val title: String, val height: Int, val fields: List<FieldDef>)

@Serializable
data class FieldDef(val title: String, val type: Int)

@Serializable
class ViewDef (val view: Map<String, String>, val widgets: List<WidgetDef>)

@Serializable
class WidgetDef(val view: Map<String, String>)
//class WidgetDef(val view: Map<String, @ContextualSerialization Any>)


object swtBuilder {

    //@ImplicitReflectionSerializer
    fun renderTest(){

        val json = Json.Default//JsonConfiguration.Stable)

        /* class containing a map style: why because the
        serializer can't seem to handle a list of maps
        but is ok with a list of classes containing a map
        catch is: it's not ok with Any as the value type, which is a bummer
         */
        val firstNameDef = WidgetDef(mapOf("title" to "First Name", "height" to "40"))
        val lastNameDef = WidgetDef(mapOf("title" to "Last Name", "height" to "40"))
        val viewDef = ViewDef(mapOf("title" to "Kernai", "height" to "400", "width" to "40"), listOf(firstNameDef, lastNameDef))

        val viewDefTest = ViewDef(mapOf("title" to "Test", "height" to "10", "width" to "300"), listOf(firstNameDef, lastNameDef))
        val viewDefs: List<ViewDef> = listOf(viewDef, viewDefTest)
        val viewData = json.encodeToString(ListSerializer(ViewDef.serializer()), viewDefs )
        println(viewData)
        val reViewData: List<ViewDef> = json.decodeFromString(ListSerializer(ViewDef.serializer()), viewData)
        reViewData.forEach { println(it.view["title"])}
        println("****************************************")


        /* data class style */
        val dataBindingFields = listOf<FieldDef>(
            FieldDef("FirstName", 0),
            FieldDef("LastName", 0))

        val testFields = listOf<FieldDef>(
            FieldDef("Summary", 1),
            FieldDef("mosculum", 4))

        val listStuff = listOf(
            WindowDef("Kernai", 100, dataBindingFields),
            WindowDef("Test", 100, testFields))

        val jsonData = json.encodeToString(ListSerializer(WindowDef.serializer()), listStuff)
        println(jsonData)
        val rehydryatedData = json.decodeFromString(ListSerializer(WindowDef.serializer()), jsonData)
        rehydryatedData.forEach {
            println("${it.title}")
            it.fields.forEach {
                println("${it.title}")
            }
        }
    }
}
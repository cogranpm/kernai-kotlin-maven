package com.parinherm.builders

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/* loads application definition from server
and is able to build a ui from it

using gson to parse the ui definitions into maps and list of maps etc

 */


object swtBuilder {

    // this is necessary because gson is a java library and has some weird kind of init thing
    inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)

   fun getTestDefinitions(): String {
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
       val formDefWire = gson.toJson(formDef)
       return formDefWire
   }

    fun renderTest() {
        val turns = Gson().fromJson<Map<String, Any>>(getTestDefinitions())
        println("${turns["title"]} ${turns["type"]}")
        val fields = turns["fields"] as List<Map<String, Any>>
        fields.forEach{
            item: Map<String, Any> -> println("${item["title"]} ${item["includeLabel"]}")
        }
    }


}

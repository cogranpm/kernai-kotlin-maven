package com.parinherm.builders

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.parinherm.server.ViewBuilder

/* loads application definition from server
and is able to build a ui from it

using gson to parse the ui definitions into maps and list of maps etc

 */


object swtBuilder {

    // this is necessary because gson is a java library and has some weird kind of init thing
    inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)

   fun getTestDefinitions(): String {

       val sm: String = HttpClient.getViews()
        return ViewBuilder.getViewDefinitions()
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

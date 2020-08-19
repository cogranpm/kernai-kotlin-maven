package com.parinherm.builders

import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object HttpClient {

    fun getViews(): String{

        GlobalScope.launch(Dispatchers.IO) {
            val jsonStr = try { URL("http://localhost:8080/views").readText() } catch (ex: Exception) { "null" }
            println(jsonStr)

        }
        return ""

    }
}
package com.parinherm.builders

import com.parinherm.ApplicationData
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object HttpClient {

    fun getViews(): String{
        // TODO better error handling and maybe make this call async
       val jsonStr = try { URL(ApplicationData.makeServerUrl("views")).readText() } catch (ex: Exception) { "null" }
      return jsonStr
       /* GlobalScope.launch(Dispatchers.IO) {
            val jsonStr = try { URL("http://localhost:8080/views").readText() } catch (ex: Exception) { "null" }
            println(jsonStr)

        }
        return ""*/

    }

}
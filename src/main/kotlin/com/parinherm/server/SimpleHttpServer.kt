package com.parinherm.server

//import com.google.gson.GsonBuilder
import com.parinherm.ApplicationData
import com.parinherm.entity.schema.ViewDefinitions
import com.sun.net.httpserver.HttpServer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.PrintWriter
import java.net.InetSocketAddress

object SimpleHttpServer {

    /*
    val server  =  HttpServer.create(InetSocketAddress(ApplicationData.serverPort), 0)

    fun start(){

        server.apply {

            // test route
            createContext("/hello") { http ->
                http.responseHeaders.add("Content-type", "text/plain")
                http.sendResponseHeaders(200, 0)
                PrintWriter(http.responseBody).use { out ->
                    out.println("Hello ${http.remoteAddress.hostName}!")
                }
            }

            // route for loading view definitions from server
            createContext("/views") { http ->
                http.responseHeaders.add("Content-type", "application/json")
                http.sendResponseHeaders(200, 0)
                //transform to json for wire format



                //val viewDefsFile = viewDefsResource.file
                //val viewDefsContent = File(viewDefsFile).readText()

                // user data format instead

                PrintWriter(http.responseBody).use { out ->

                    /* used to load all views at once, not any more
                    these are in database instead
                    out.println(ViewDefinitions.load())
                     */
                }

                //val gson = GsonBuilder().create()
                //PrintWriter(http.responseBody).use { out ->
                //    out.println(gson.toJson(ViewDefinitions.makeDefinitions()))
                //}
            }
            start()
        }
    }

    fun stop() {
        server.stop(0)
    }
     */
}
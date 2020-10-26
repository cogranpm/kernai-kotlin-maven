package com.parinherm.server

import com.google.gson.GsonBuilder
import com.parinherm.form.definitions.ViewDef
import com.sun.net.httpserver.HttpServer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.PrintWriter
import java.net.InetSocketAddress

object SimpleHttpServer {

    val server  =  HttpServer.create(InetSocketAddress(8080), 0)

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

                /******** transition to kotlinx from gson *************/
                val format = Json { prettyPrint = true }
                val x = format.encodeToString(ViewDefinitions.makeNotebooks())
                println(x)
                val item = format.decodeFromString<ViewDef>(x)
                println(item.title)

                /************* old way was gson *************/
                val gson = GsonBuilder().create()
                PrintWriter(http.responseBody).use { out ->
                    out.println(gson.toJson(ViewDefinitions.makeDefinitions()))
                }
            }

            /*
            createContext("/views2") { http ->
                http.responseHeaders.add("Content-type", "application/json")
                http.sendResponseHeaders(200, 0)
                //transform to json for wire format
                val gson = GsonBuilder().create()
                PrintWriter(http.responseBody).use { out ->
                    //out.println(gson.toJson(ViewDefinitions.makeNotebooks()))
                    out.println(gson.toJson("farts"))
                }
            }

             */
            start()
        }
    }

    fun stop() {
        server.stop(0)
    }
}
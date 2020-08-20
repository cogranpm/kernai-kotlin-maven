package com.parinherm.server

import com.sun.net.httpserver.HttpServer
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
                PrintWriter(http.responseBody).use { out ->
                    out.println(ViewBuilder.makeDefinitions())
                }
            }
            start()
        }
    }

    fun stop() {
        server.stop(0)
    }
}
package com.parinherm.server

import spark.Spark.*


object SparkServer {

    fun run() {
        get("/hello") { req, res -> "Hello World" }
    }


    fun stop(){
        stop()
    }
}
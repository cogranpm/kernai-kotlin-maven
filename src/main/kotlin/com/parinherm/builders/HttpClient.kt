package com.parinherm.builders

import com.parinherm.ApplicationData
import java.net.URL
import com.parinherm.errors.Result

object HttpClient {

    suspend fun getViews(): Result<String, Exception>{
        return try {
            //Result.Success<String>(URL(ApplicationData.makeServerUrl("views")).readText())
            Result.Success<String>("not using this anymore")
        } catch (e: Exception){
            Result.Error<String>(e)
        }
    }

}
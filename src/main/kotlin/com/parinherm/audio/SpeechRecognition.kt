package com.parinherm.audio

import com.parinherm.ApplicationData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.vosk.LibVosk
import org.vosk.LogLevel
import org.vosk.Model
import org.vosk.Recognizer
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths

object SpeechRecognition {

    private const val sampleRate = 120000f
    private val modelPath: String
    private val jsonFormat = Json { isLenient = true }
    private var model: Model? = null
    public var recognizer: Recognizer? = null
    private var opened = false

    init {
        LibVosk.setLogLevel(LogLevel.DEBUG)
        val currentPath = Paths.get("").toAbsolutePath().normalize()
        val parentPath = currentPath.parent
        var tmpModelPath = "${parentPath.toString()}${File.separator}model"
        var modelPathFile = File(tmpModelPath)
        if(modelPathFile.isDirectory){
           modelPath = tmpModelPath
        } else {
            tmpModelPath = "${currentPath.toString()}${File.separator}model"
            modelPathFile = File(tmpModelPath)
            if(modelPathFile.isDirectory){
                modelPath = tmpModelPath
            } else {
                throw FileNotFoundException("Model Path could not be found")
            }
        }
    }

    fun open(){
        if(model == null){
            model = Model(modelPath)
        }
        if(recognizer == null){
            recognizer = Recognizer(model, sampleRate)
        }
        opened = true
    }

    fun recognize(audio: ByteArray, length: Int) : String {
       return if(recognizer != null){
           if (recognizer!!.acceptWaveForm(audio, length)) {
               //val data = jsonFormat.decodeFromString<RecognizerText>(recognizer!!.result)
               val data = jsonFormat.decodeFromString<RecognizerText>(recognizer!!.finalResult)
               return data.text
           } else {
               //println(recognizer!!.partialResult)
               return ""
           }
       } else {
           return ""
       }
    }

    fun close(){
        try {
            if(recognizer != null){
                recognizer?.close()
            }
            if(model != null){
                model?.close()
            }
        } catch (e: Exception){
            ApplicationData.logError(e, "Error in close of SpeechRecognition")
        } finally {
            opened = false
        }
    }
}
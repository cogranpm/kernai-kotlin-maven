package com.parinherm.audio

import com.parinherm.ApplicationData
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

object AudioClient {

    private var openState = false
    private val capture = AudioCapture()
    private lateinit var job: Job
    var processingFunction: ((String, ByteArray, Int) -> Unit)? = null


    fun open(){
        if(openState){
           return
        }
        AudioPlayer.open()
        openState = true
        job = capture.run().cancellable().onEach { value: AudioDataResult ->
            //val (peak, rms) = AudioConverter.calculatePeakAndRms(AudioConverter.encodeToSample(audioData, audioLength))
            //AudioPlayer.play(value.audioData.first, value.audioData.second)
            val (audio, length) = value.audioData
            val result = SpeechRecognition.recognize(audio, length)

            ApplicationData.mainWindow.shell.display.asyncExec {
                if(processingFunction != null){
                    processingFunction?.let { it(result, audio, length) }
                }
                // println("ui thread stuff")
            }
        }.catch { e ->
            println("Caught $e")
        }
            .launchIn(CaptureScope)
    }

    fun close(){
       if(openState){
           AudioPlayer.close()
           job.cancel()
       }

    }
}
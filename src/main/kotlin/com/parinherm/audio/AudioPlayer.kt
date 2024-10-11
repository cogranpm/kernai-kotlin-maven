package com.parinherm.audio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.sound.sampled.*
import kotlin.coroutines.CoroutineContext

object PlayerScope: CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO
}

object AudioPlayer {
    private var speakers: SourceDataLine
    private val format = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 60000f, 16, 2, 4, 44100f, false)
    private val dataLineInfo: DataLine.Info = DataLine.Info(SourceDataLine::class.java, format)
    init {
        speakers = AudioSystem.getLine(dataLineInfo) as SourceDataLine
    }

    fun open(){
        speakers.open(format)
        speakers.start()
    }

    fun play(audio: ByteArray, length: Int){
        try {
            PlayerScope.launch {
                speakers.write(audio, 0, length)
            }
        } catch (e: Exception){
           println(e.message)
        }
    }

    fun close(){
        speakers.drain()
        speakers.close()
    }
}
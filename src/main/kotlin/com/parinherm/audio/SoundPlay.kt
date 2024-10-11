package com.parinherm.audio

import com.parinherm.ApplicationData
import java.io.BufferedInputStream
import javax.sound.sampled.AudioSystem

object SoundPlay {

    fun play(name: String){
        try {
            val audioSrc =  javaClass.getResourceAsStream("/sounds/${name}.wav")
            val bufferedIn = BufferedInputStream(audioSrc)
            val audioInput = AudioSystem.getAudioInputStream(bufferedIn)
            val clip = AudioSystem.getClip()
            clip.open(audioInput)
            clip.start()
        } catch(e: Exception){
            ApplicationData.logError(e, "Error playing sound: ${name}.wav")
        }
    }
}
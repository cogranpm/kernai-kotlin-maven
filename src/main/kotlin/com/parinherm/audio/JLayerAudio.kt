package com.parinherm.audio

import javazoom.jl.player.FactoryRegistry
import javazoom.jl.player.JavaSoundAudioDeviceFactory
import javazoom.jl.player.jlp
import java.util.*
import kotlin.concurrent.schedule


object JLayerAudio {
    fun play(){
        val player = jlp.createInstance(arrayOf("B:\\Paul\\books\\audio-books fiction\\Stalker\\Stalker-Part01.mp3"))
        player.setAudioDevice(
            FactoryRegistry.systemRegistry().createAudioDevice(JavaSoundAudioDeviceFactory::class.java)
        )
        Timer().schedule(5000) {
            player.stop()
        }
        player.play();

        /*
        player.setAudioDevice(
            FactoryRegistry.systemRegistry().createAudioDevice(BaseSoundAudioDeviceFactory::class.java)
        )
        (player.setAudioDevice() as BaseSoundAudioDevice).setVolume(0.2f)
        Timer().schedule(2000) {
            player.stop()
        }
        player.play()
         */
    }
}
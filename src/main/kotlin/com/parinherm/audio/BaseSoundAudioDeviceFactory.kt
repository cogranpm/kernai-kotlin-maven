package com.parinherm.audio

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.AudioDeviceFactory;

class BaseSoundAudioDeviceFactory : AudioDeviceFactory() {
    override fun createAudioDevice(): AudioDevice {
        return BaseSoundAudioDevice()
    }
}
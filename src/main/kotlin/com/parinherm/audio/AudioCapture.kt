package com.parinherm.audio

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.sound.sampled.*

data class RecognizerState(var paused: Boolean = false)

class AudioCapture {
    private var state: RecognizerState = RecognizerState()
    private val SPEAKERS_ON = false
    private var microphone: TargetDataLine


    /* this works - high quality but large */
    val audioFormat = AudioFormat.Encoding.PCM_SIGNED
    val sampleRate = 60000f
    val sampleSizeInBits = 16
    val channels = 2
    val bigEndian = false
    val frameSize = 4
    val frameRate = 44100f

    private val format = AudioFormat(audioFormat, sampleRate, sampleSizeInBits, channels, frameSize, frameRate, bigEndian)
    private val info = DataLine.Info(TargetDataLine::class.java, format)
    private val CHUNK_SIZE = 1024

    init {

        microphone = AudioSystem.getLine(info) as TargetDataLine
        microphone.open(format)
        microphone.start()

        /*
        val infos = AudioSystem.getSourceLineInfo(Line.Info(SourceDataLine::class.java))
        for (info in infos){
            if(info is DataLine.Info){
                var formats = info.formats
                for (format in formats){
                    var channels = format.channels
                }
            }
        }
         */
    }

    fun run(): Flow<AudioDataResult> = flow {
        //val audioQueue = arrayListOf<Pair<ByteArray, Int>>()
        try {

            var numBytesRead: Int
            //val out = ByteArrayOutputStream()
            val b = ByteArray(4096)
            while (true) {

                if (!state.paused) {
                    var accepted = false
                    //val available = microphone.available()
                   numBytesRead = microphone.read(b, 0, CHUNK_SIZE)
                   //numBytesRead = microphone.read(b, 0, available)
                    //out.write(b, 0, numBytesRead)
                    //val (peak, rms) = AudioConverter.calculatePeakAndRms(AudioConverter.encodeToSample(b, numBytesRead))
                    //println("Peak $peak rms: $rms")
                    emit(AudioDataResult(Pair(b.copyOf(), numBytesRead)))
                    //audioQueue.add(Pair(b.copyOf(), numBytesRead))

                    if (SPEAKERS_ON) {
                        AudioPlayer.play(b, numBytesRead)
                        //AudioPlayer.play(out.toByteArray(), out.size())
                    }
                } else {
                    delay(500)
                }
            }
        } catch (e: Exception) {
            println("we have stopped listening")
        } finally {
            cleanup()
        }
    }

    private fun cleanup() {
        microphone.stop()
        microphone.close()
    }

}


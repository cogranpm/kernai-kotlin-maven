package com.parinherm.audio.speech

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.vosk.Recognizer
import java.io.ByteArrayOutputStream
import javax.sound.sampled.*

class SpeechPipelineService(
    private val recognizer: Recognizer,
    private val silenceThreshold: Double = 500.0,
    private val silenceTimeoutMillis: Long = 1500,
    private val chunkSize: Int = 1024,
    private val sampleRate: Float = 60000f,
    private val onFinalResult: (String, ByteArray, Int) -> Unit,
    private val onPartialResult: (String) -> Unit = {}
) {

    private val format = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, 16, 2, 4, 44100f, false)
    private val info = DataLine.Info(TargetDataLine::class.java, format)
    private var microphone: TargetDataLine = AudioSystem.getLine(info) as TargetDataLine

    private var audioBuffer = ByteArrayOutputStream()
    private var lastSpeechTime = System.currentTimeMillis()

    fun start(): Flow<Unit> = flow {
        microphone.open(format)
        microphone.start()
        val buffer = ByteArray(chunkSize)

        while (true) {
            val numBytesRead = microphone.read(buffer, 0, chunkSize)
            val rms = calculateRms(buffer, numBytesRead)

            if (rms > silenceThreshold) {
                lastSpeechTime = System.currentTimeMillis()
                audioBuffer.write(buffer, 0, numBytesRead)

                if (recognizer.acceptWaveForm(buffer, numBytesRead)) {
                    val finalText = recognizer.finalResult
                    onFinalResult(finalText, audioBuffer.toByteArray(), audioBuffer.size())
                    audioBuffer.reset()
                } else {
                    val partialText = recognizer.partialResult
                    onPartialResult(partialText)
                }
            } else {
                val silenceDuration = System.currentTimeMillis() - lastSpeechTime
                if (silenceDuration > silenceTimeoutMillis && audioBuffer.size() > 0) {
                    val finalText = recognizer.finalResult
                    onFinalResult(finalText, audioBuffer.toByteArray(), audioBuffer.size())
                    audioBuffer.reset()
                }
            }

            emit(Unit)
        }
    }.flowOn(Dispatchers.IO)

    fun stop() {
        microphone.stop()
        microphone.close()
    }

    private fun calculateRms(buffer: ByteArray, length: Int): Double {
        var sum = 0.0
        for (i in 0 until length step 2) {
            val sample = ((buffer[i + 1].toInt() shl 8) or (buffer[i].toInt() and 0xFF)).toShort()
            sum += sample * sample
        }
        return Math.sqrt(sum / (length / 2))
    }
}

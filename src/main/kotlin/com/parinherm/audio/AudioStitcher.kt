/*****************************
 * takes an array of utterances from microphone
 * and sends back 1 big utterance combined
 */

package com.parinherm.audio

import java.io.ByteArrayOutputStream

object AudioStitcher {

    fun stitch(audioList: List<AudioDataResult>): Pair<ByteArray, Int> {
        val output = ByteArrayOutputStream()
        audioList.forEach {
            val (audio, length) = it.audioData
            output.write(audio, 0, length)
        }
        val audioData = output.toByteArray()
        return Pair(audioData, output.size())
    }

}
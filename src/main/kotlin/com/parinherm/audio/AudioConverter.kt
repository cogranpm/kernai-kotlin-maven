package com.parinherm.audio

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs

object AudioConverter {

    fun encodeToSample(srcBuffer: ByteArray, numBytes: Int): ShortArray {
        val tempBuffer = ByteArray(2)
        val nSamples = numBytes / 2
        val samples = ShortArray(nSamples) // 16-bit signed value
        for (i in 0 until nSamples) {
            tempBuffer[0] = srcBuffer[2 * i]
            tempBuffer[1] = srcBuffer[2 * i + 1]
            samples[i] = bytesToShort(tempBuffer)
        }
        return samples
    }

    private fun bytesToShort(buffer: ByteArray): Short {
        val bb: ByteBuffer = ByteBuffer.allocate(2)
        //bb.order(ByteOrder.BIG_ENDIAN)
        bb.order(ByteOrder.LITTLE_ENDIAN)
        bb.put(buffer[0])
        bb.put(buffer[1])
        return bb.getShort(0)
    }

    fun calculatePeakAndRms(samples: ShortArray): Pair<Double, Double> {
        var sumOfSampleSq = 0.0 // sum of square of normalized samples.
        var peakSample = 0.0 // peak sample.
        for (sample in samples) {
            val normSample = sample.toDouble() / 32767 // normalized the sample with maximum value.
            sumOfSampleSq += (normSample * normSample)
            if (Math.abs(sample.toInt()) > peakSample) {
                peakSample = abs(sample.toInt()).toDouble()
            }
        }
        val rms = 10 * Math.log10(sumOfSampleSq / samples.size)
        val peak = 20 * Math.log10(peakSample / 32767)
        return Pair<Double, Double>(peak, rms)
    }

    /******** another possible algorithm **********
    private fun volumeRMS(raw: ShortArray): Short {
        var sum: Short = 0
        if (raw.size == 0) {
            return sum
        } else {
            for (ii: Int = 0; ii < raw.length; ii++) {
                sum += raw[ii]
            }
        }
        val average = sum / raw.length

        var sumMeanSquare = 0
        for (var ii= 0; ii < raw.length; ii++) {
            sumMeanSquare += Math.pow(raw[ii] - average, 2 d)
        }
        val averageMeanSquare = sumMeanSquare / raw.length
        val rootMeanSquare = Math.sqrt(averageMeanSquare)
        return rootMeanSquare;
    }
    *******************/

}
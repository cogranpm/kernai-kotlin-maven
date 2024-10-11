package com.parinherm.audio

import javazoom.jl.decoder.JavaLayerException
import javazoom.jl.player.AudioDeviceBase
import javax.sound.sampled.*
import kotlin.math.log10


class BaseSoundAudioDevice : AudioDeviceBase() {

    private var source: SourceDataLine? = null
    private var fmt: AudioFormat? = null
    private var byteBuf = ByteArray(4096)

    protected fun setAudioFormat(fmt0: AudioFormat?) {
        fmt = fmt0
    }

    protected fun getAudioFormat(): AudioFormat {
        val fmt = AudioFormat(
            44100f,
            16,
            2,
            true,
            false
        )

        return fmt
    }

    protected fun getSourceLineInfo(): DataLine.Info {
        val fmt = getAudioFormat()
        val info = DataLine.Info(SourceDataLine::class.java, fmt)
        return info
    }

    @Throws(JavaLayerException::class)
    fun open(fmt: AudioFormat?) {
        if (!isOpen) {
            setAudioFormat(fmt)
            openImpl()
            isOpen = true
        }
    }

    fun setVolume(gain: Float) {
        this.gain = (log10(gain.toDouble()) * 20.0).toFloat()
    }

    /**  */
    private fun setLineGain() {
        val volControl = source!!.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
        volControl.value = gain
    }

    @Throws(JavaLayerException::class)
    public override fun openImpl() {
    }

    // createSource fix.
    @Throws(JavaLayerException::class)
    fun createSource() {
        var t: Throwable? = null
        try {
            val line = AudioSystem.getLine(getSourceLineInfo())
            if (line is SourceDataLine) {
                source = line
                source!!.open(fmt)
                setLineGain()

                source!!.start()
            }
        } catch (ex: RuntimeException) {
            ex.printStackTrace(System.err)
            t = ex
        } catch (ex: LinkageError) {
            ex.printStackTrace(System.err)
            t = ex
        } catch (ex: LineUnavailableException) {
            ex.printStackTrace(System.err)
            t = ex
        }
        if (source == null) {
            throw JavaLayerException("cannot obtain source audio line", t)
        }
    }

    fun millisecondsToBytes(fmt: AudioFormat, time: Int): Int {
        return (time * (fmt.sampleRate * fmt.channels * fmt.sampleSizeInBits) / 8000.0).toInt()
    }

    override fun closeImpl() {
        source?.close()
    }

    @Throws(JavaLayerException::class)
    override fun writeImpl(samples: ShortArray, offs: Int, len: Int) {
        if (source == null) {
            createSource()
        }

        val b = toByteArray(samples, offs, len)
        source!!.write(b, 0, len * 2)
    }

    protected fun getByteArray(length: Int): ByteArray {
        if (this.byteBuf.size < length) {
            this.byteBuf = ByteArray(length + 1024)
        }
        return this.byteBuf
    }

    protected fun toByteArray(samples: ShortArray, offs: Int, len: Int): ByteArray {
        var offs = offs
        var len = len
        val b = getByteArray(len * 2)
        var idx = 0
        var s: Short
        while (len-- > 0) {
            s = samples[offs++]
            b[idx++] = s.toByte()
            b[idx++] = (s.toInt() ushr 8).toByte()
        }
        return b
    }

    override fun flushImpl() {
        source?.drain()
    }
    /**  */
    private var gain = 6.0206f

    override fun getPosition(): Int {
        var pos = 0
        if (source != null) {
            pos = (source!!.microsecondPosition / 1000).toInt()
        }
        return pos
    }
}
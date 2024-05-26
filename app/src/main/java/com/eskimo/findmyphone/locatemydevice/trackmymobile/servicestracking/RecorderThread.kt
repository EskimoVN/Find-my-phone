package com.eskimo.findmyphone.locatemydevice.trackmymobile.servicestracking

import android.annotation.SuppressLint
import android.media.AudioRecord

@SuppressLint("MissingPermission")
class RecorderThread : Thread() {
    private val audioEncoding = 2
    private val channelConfiguration = 16
    private val frameByteSize = 2048
    private val sampleRate = 44100
    private var audioRecord: AudioRecord
    private var buffer: ByteArray
    @Volatile private var isRecording = false

    init {
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfiguration, audioEncoding)
        audioRecord = AudioRecord(1, sampleRate, channelConfiguration, audioEncoding, bufferSize)
        buffer = ByteArray(frameByteSize)
    }

    fun getAudioRecord(): AudioRecord {
        return audioRecord
    }

    fun isRecording(): Boolean {
        return isAlive && isRecording
    }

    fun startRecording() {
        try {
            audioRecord.startRecording()
            isRecording = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopRecording() {
        try {
            audioRecord.stop()
            audioRecord.release()
            isRecording = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getFrameBytes(): ByteArray? {
        audioRecord.read(buffer, 0, frameByteSize)
        var sum = 0
        for (i in buffer.indices step 2) {
            val value = ((buffer[i + 1].toInt() shl 8) or (buffer[i].toInt() and 0xFF)).toShort()
            sum += Math.abs(value.toInt())
        }
        val averageAmplitude = sum / (frameByteSize / 2)
        return if (averageAmplitude < 30) null else buffer
    }

    override fun run() {
        startRecording()
    }

    override fun interrupt() {
        super.interrupt()
        stopRecording()
    }
}

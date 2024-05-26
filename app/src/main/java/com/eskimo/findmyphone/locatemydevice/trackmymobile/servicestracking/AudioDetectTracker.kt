package com.eskimo.findmyphone.locatemydevice.trackmymobile.servicestracking

import android.annotation.SuppressLint
import android.media.AudioRecord
import android.util.Log
import com.musicg.api.ClapApi
import com.musicg.api.WhistleApi
import com.musicg.wave.WaveHeader
import kotlin.math.abs

@SuppressLint("MissingPermission")
class AudioDetectTracker(private val listener: OnAudioEventListener) {
    private val audioEncoding = 2
    private val channelConfiguration = 16
    private val sampleRate = 44100 // Tần số mẫu
    private val frameByteSize = 2048
    private var bufferSize: Int = 0
    private var buffer: ByteArray
    private var isListening = false
    private var audioRecord: AudioRecord

    init {
        val bufferSize =
            AudioRecord.getMinBufferSize(sampleRate, channelConfiguration, audioEncoding)
        audioRecord = AudioRecord(1, sampleRate, channelConfiguration, audioEncoding, bufferSize)
        buffer = ByteArray(frameByteSize)
    }

    interface OnAudioEventListener {
        fun onClapDetected()
        fun onWhistleDetected()
    }

    fun startListening() {
        isListening = true
        audioRecord.startRecording()
        Thread {
            detectAudio()
        }.start()
    }

    fun stopListening() {
        isListening = false
        audioRecord.stop()
    }

    private fun getFrameBytes(): ByteArray? {
        val buffer = ByteArray(frameByteSize)
        var i: Int
        var i2 = 0
        audioRecord.read(buffer, 0, this.frameByteSize)
        var i3 = 0
        while (true) {
            i = this.frameByteSize
            if (i2 >= i) {
                break
            }
            val bArr: ByteArray = buffer
            i3 += abs((bArr[i2 + 1].toInt() shl 8 or bArr[i2].toInt()).toShort().toInt())
            i2 += 2
        }
        return if ((i3 / i / 2).toFloat() < 30.0f) {
            null
        } else buffer
    }

    private fun detectAudio() {
        val buffer = ByteArray(bufferSize)
        val waveHeader = WaveHeader()
        var whistleApi = WhistleApi(waveHeader)
        var clapApi = ClapApi(waveHeader)
        while (isListening) {
            val read = audioRecord.read(buffer, 0, bufferSize)
            val frameByte: ByteArray? = getFrameBytes()
            if (frameByte != null) {
  //              val isWhistle = whistleApi.isWhistle(frameByte)
//                val isClap = clapApi.isClap(frameByte)
//                if(isWhistle || isClap){
//                    Log.d("LucTV", "detectAudio: whistle : $isWhistle -- cal : $isClap")
//                }
            }


//
//            if (read > 0) {
//                if (getFrameBytes() != null) {
//                    val i2 =
//                        if (audioRecord.audioFormat == 2) 16 else if (audioRecord.audioFormat == 3) 8 else 0
//                    val i = if (audioRecord.channelConfiguration == 16) 1 else 0
//                    val waveHeader = WaveHeader()
//                    waveHeader.channels = i
//                    waveHeader.bitsPerSample = i2
//                    waveHeader.sampleRate = audioRecord.sampleRate
//                    whistleApi = WhistleApi(waveHeader)
//                    clapApi = ClapApi(waveHeader)
//                    // if (whistleApi.isWhistle(buffer) || clapApi.isClap(buffer)) {
//                    Log.d(
//                        "LucTV",
//                        "detectAudio: isWhistle : ${whistleApi.isWhistle(buffer)} -- isClap : ${
//                            clapApi.isClap(buffer)
//                        }"
//                    )
//                    //     }
//                }
//            }
        }
    }
}

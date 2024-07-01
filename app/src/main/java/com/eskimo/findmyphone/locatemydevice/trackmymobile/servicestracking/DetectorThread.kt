package com.eskimo.findmyphone.locatemydevice.trackmymobile.servicestracking

import android.media.AudioFormat
import com.musicg.api.ClapApi
import com.musicg.api.WhistleApi
import com.musicg.wave.WaveHeader
import java.util.LinkedList

class DetectorThread(
    private val recorder: RecorderThreadJava,
) : Thread() {

    @Volatile
    private var _thread: Thread? = null

    @Volatile
    private var running = true
    private var numWhistles = 0
    private var numClaps = 0
    private var onSignalsDetectedListener: OnSignalsDetectedListener? = null
    private val waveHeader: WaveHeader
    private val whistleApi: WhistleApi
    private val clapApi: ClapApi
    private val whistleCheckLength = 1
        private val clapCheckLength = 1
    private val whistlePassScore = 1
    private val clapPassScore = 1
    private val whistleResultList = LinkedList<Boolean>()
    private val clapResultList = LinkedList<Boolean>()

    init {
        val audioRecord = recorder.audioRecord
        val bitsPerSample =
            if (audioRecord.audioFormat == AudioFormat.ENCODING_PCM_16BIT) 16 else if (audioRecord.audioFormat == AudioFormat.ENCODING_PCM_8BIT) 8 else 0
        val channels = if (audioRecord.channelConfiguration == 16) 1 else 0
        waveHeader = WaveHeader().apply {
            setChannels(channels)
            setBitsPerSample(bitsPerSample)
            sampleRate = audioRecord.sampleRate
        }
        whistleApi = WhistleApi(waveHeader)
        clapApi = ClapApi(waveHeader)
    }

    private fun initBuffer() {
        numWhistles = 0
        numClaps = 0
        whistleResultList.clear()
        clapResultList.clear()
        repeat(whistleCheckLength) { whistleResultList.add(false) }
        repeat(clapCheckLength) { clapResultList.add(false) }
    }

    override fun start() {
        if (_thread == null) {
            _thread = Thread(this)
            running = true
            _thread?.start()
        }
    }

    fun stopDetection() {
        running = false
        _thread?.interrupt()
        _thread = null

    }

    override fun run() {
        try {
            initBuffer()
            while (running) {
                val currentThread = Thread.currentThread()
                while (_thread == currentThread) {
                    val frameBytes = recorder.frameBytes
                    frameBytes?.let {
                        val isWhistle = whistleApi.isWhistle(it)
                        val isClap = clapApi.isClap(it)
                        if (whistleResultList.first) numWhistles--
                        if (clapResultList.first) numClaps--
                        whistleResultList.removeFirst()
                        clapResultList.removeFirst()
                        whistleResultList.add(isWhistle)
                        clapResultList.add(isClap)
                        if (isWhistle) numWhistles++
                        if (isClap) numClaps++
                        if (numWhistles >= whistlePassScore) {
                            initBuffer()
                           // onWhistleDetected()
                        }
                       //Log.d("LucTV", "run: $numClaps")
                        if (numClaps >= clapPassScore) {
                            initBuffer()
                            onClapDetected()
                        }
                    } ?: run {
                        if (whistleResultList.first) numWhistles--
                        whistleResultList.removeFirst()
                        whistleResultList.add(false)
                        if (clapResultList.first) numClaps--
                        clapResultList.removeFirst()
                        clapResultList.add(false)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onClapDetected() {
        //  Log.d("LucTV", "onClapDetected: $onSignalsDetectedListener")
        onSignalsDetectedListener?.onClapDetected()
    }

    private fun onWhistleDetected() {
        //      Log.d("LucTV", "onWhistleDetected: $onSignalsDetectedListener")
        onSignalsDetectedListener?.onWhistleDetected()
    }

    fun setOnSignalsDetectedListener(listener: OnSignalsDetectedListener) {
        this.onSignalsDetectedListener = listener
    }

    override fun interrupt() {
        super.interrupt()
    }
}

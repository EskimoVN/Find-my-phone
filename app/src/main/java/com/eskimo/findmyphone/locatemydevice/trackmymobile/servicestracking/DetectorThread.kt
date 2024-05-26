package com.eskimo.findmyphone.locatemydevice.trackmymobile.servicestracking

import android.util.Log
import com.musicg.api.ClapApi
import com.musicg.api.WhistleApi
import com.musicg.wave.WaveHeader
import java.util.LinkedList

class DetectorThread(private val recorder: RecorderThread, private var clapValue: String) :
    Thread() {

    @Volatile
    private var _thread: Thread? = null
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
        val audioRecord = recorder.getAudioRecord()
        val bitsPerSample =
            if (audioRecord.audioFormat == 2) 16 else if (audioRecord.audioFormat == 3) 8 else 0
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
        _thread = Thread(this)
        _thread?.start()
    }

    fun stopDetection() {
        _thread = null
    }

    override fun run() {
        try {
            initBuffer()
            val currentThread = Thread.currentThread()
            while (_thread == currentThread) {
                val frameBytes = recorder.getFrameBytes()
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
                    clapValue = "YES"
                    if(isWhistle == true){
                        Log.d("LucTV", "run: true")
                    }else {
                        Log.d("LucTV", "run: false")
                    }
                    if (numWhistles >= whistlePassScore) {
                        Log.e("Sound", "Detected")
                        initBuffer()
                        if (clapValue == "ON") {
                            onWhistleDetected()
                        }
                    }
                    if (numClaps >= clapPassScore) {
                        Log.e("Sound", "Detected")
                        initBuffer()
                        if (clapValue == "YES") {
                            onWhistleDetected()
                        }
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onClapDetected() {
        Log.d("TAG", "onClapDetected: ")
    }

    private fun onWhistleDetected() {
        onSignalsDetectedListener?.onWhistleDetected()
    }

    fun setOnSignalsDetectedListener(listener: OnSignalsDetectedListener) {
        this.onSignalsDetectedListener = listener
    }

    override fun interrupt() {
        super.interrupt()
    }
}

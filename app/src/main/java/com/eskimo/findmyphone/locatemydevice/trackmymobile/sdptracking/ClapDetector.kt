package com.eskimo.findmyphone.locatemydevice.trackmymobile.sdptracking

import android.util.Log
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.onsets.OnsetHandler
import be.tarsos.dsp.onsets.PercussionOnsetDetector

class ClapDetector(private val isDebug: Boolean = true) {

    private var claps  =-1
    private lateinit var dispatcher: AudioDispatcher
    private lateinit var thread: Thread

    init {
        log("init")
    }

    fun detectClapAnd(
        tapThreshold: Int = 1,
        action: () -> (Unit)
    ) {
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0)
        val threshold = 8.0
        val sensitivity = 35.0
        val mPercussionDetector = PercussionOnsetDetector(
            22050f, 1024,
            OnsetHandler { time, salience ->
                claps++
                if (claps == tapThreshold) {
                    resetClaps()
                    action()
                }

            }, sensitivity, threshold
        )
        dispatcher.addAudioProcessor(mPercussionDetector)

        thread = Thread(dispatcher, "Audio Dispatcher")
        thread.start()
        Log.d("LucTV", "detectClapAnd: start")
    }

    private fun log(message: String) {
        if (isDebug) {
            Log.d("ClapDetector", message)
        }
    }

    fun cancel() {
        log("cancel")
        try {
            dispatcher.stop()
            thread.interrupt()
        } catch (exp: Exception) {
            log("error doing cancel: ${exp.localizedMessage}")
        }
    }

    private fun resetClaps() {
        claps = 0
    }
}
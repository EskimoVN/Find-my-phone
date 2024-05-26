package com.eskimo.findmyphone.locatemydevice.trackmymobile.servicestracking

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class AudioDetectService : Service() {
    private lateinit var audioDetector: AudioDetectTracker
    private lateinit var detectorThread: DetectorThread

    override fun onCreate() {
        super.onCreate()
        val recorderThread = RecorderThread();
        recorderThread.startRecording()
        Log.d("LucTV1", "onCreate: ${recorderThread.getFrameBytes()}")
        detectorThread = DetectorThread(recorderThread,"YES")


        audioDetector = AudioDetectTracker(object : AudioDetectTracker.OnAudioEventListener {
            override fun onClapDetected() {
                Log.d("LucTV", "Clap detected!")
            }

            override fun onWhistleDetected() {
                Log.d("LucTV", "Whistle detected!")
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        audioDetector.startListening()
        detectorThread.start()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        audioDetector.stopListening()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

package com.eskimo.findmyphone.locatemydevice.trackmymobile.servicestracking

import android.app.KeyguardManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.eskimo.findmyphone.locatemydevice.trackmymobile.R
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.MyApplication
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.home.views.AppNotificationManager
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.home.views.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudioDetectService : Service() {
    private lateinit var detectorThread: DetectorThread
    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null
    private var isFlashOn = false
    private var flashJob: Job? = null
    private var vibrator: Vibrator? = null
    private var vibrateJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        val recorderThread = RecorderThread();
        recorderThread.startRecording()
        detectorThread = DetectorThread(recorderThread)
        detectorThread.setOnSignalsDetectedListener(object : OnSignalsDetectedListener {
            override fun onWhistleDetected() {
                handleDetected(false)
            }

            override fun onClapDetected() {
                handleDetected(true)
            }

        })
        createNotificationChannel()
        val notification = createNotification()
        startForeground(1, notification)
    }

    private fun handleDetected(isClap: Boolean) {
        requestCamera()
        requestVibrate()
        val keyguardManager =
            MyApplication.getApplication().applicationContext.getSystemService(
                Context.KEYGUARD_SERVICE
            ) as KeyguardManager
        val isDeviceLocked: Boolean =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                keyguardManager.isDeviceLocked
            } else {
                keyguardManager.isKeyguardLocked
            }
        if (isDeviceLocked) {
            AppNotificationManager.sendLockscreenWidgetNotification(
                context = MyApplication.getApplication().applicationContext,
            )
        } else {
            sendNotification()
        }
    }


    private fun sendNotification() {
        val context = MyApplication.getApplication().applicationContext
        val channelId = "channel_id"
        val channelName = "Channel Name"
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.icon_power)
            .setContentTitle("Hi, phone clapped")
            .setContentText("I'm here, do you see me ?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(333, notificationBuilder.build())
    }

    private fun requestCamera() {
        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            cameraId = cameraManager.cameraIdList[0]
            startFlashing()
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun startFlashing() {
        flashJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                toggleFlashlight()
                delay(1000L)  // Nháy đèn mỗi giây
            }
        }
    }

    private fun toggleFlashlight() {
        try {
            cameraId?.let {
                cameraManager.setTorchMode(it, isFlashOn)
                isFlashOn = !isFlashOn
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun requestVibrate() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        startVibrating()
    }

    private fun startVibrating() {
        vibrateJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                toggleVibration()
                delay(1000L)  // Rung mỗi giây
            }
        }
    }

    private fun toggleVibration() {
        val DELAY = 0
        val VIBRATE = 1000
        val SLEEP = 1000
        val START = 0
        val vibratePattern = longArrayOf(DELAY.toLong(), VIBRATE.toLong(), SLEEP.toLong())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(vibratePattern, START))
        } else {
            vibrator?.vibrate(vibratePattern, START)
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        detectorThread.start()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        flashJob?.cancel()
        if (isFlashOn) {
            try {
                cameraId?.let { cameraManager.setTorchMode(it, false) }
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
        vibrateJob?.cancel()
        vibrator?.cancel()

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "AudioDetectServiceChannel",
                "Audio Detect Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, HomeActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, "AudioDetectServiceChannel")
            .setContentTitle("Audio Detect Service")
            .setContentText("Listening for audio events")
            .setSmallIcon(R.drawable.icon_settings)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)  // Makes the notification non-dismissible
            .setPriority(NotificationCompat.PRIORITY_HIGH)  // For higher visibility
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }
}

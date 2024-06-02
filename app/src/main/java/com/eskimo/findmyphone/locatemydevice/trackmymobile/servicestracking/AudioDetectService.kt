package com.eskimo.findmyphone.locatemydevice.trackmymobile.servicestracking

import android.annotation.SuppressLint
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
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.eskimo.findmyphone.locatemydevice.trackmymobile.R
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.MyApplication
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.SharedPreferencesManager
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
    private var mediaPlayer: MediaPlayer? = null
    private var isPlayingDetect = false
    private var recorderThread: RecorderThread? = null
    override fun onCreate() {
        super.onCreate()
        recorderThread = RecorderThread();
        recorderThread?.startRecording()
        detectorThread = DetectorThread(recorderThread!!)
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
        if (!isPlayingDetect) {
            isPlayingDetect = true
            requestCamera()
            requestVibrate()
            openAudio()
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
    }

    private fun resetDataService() {
        flashJob?.cancel()
        flashJob = null
        try {
            cameraId?.let { cameraManager.setTorchMode(it, false) }
            isFlashOn = false
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        vibrateJob?.cancel()
        vibrateJob = null
        vibrator?.cancel()
        vibrator = null
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPlayingDetect = false
        recorderThread?.stopRecording()
        detectorThread.stopDetection()
    }

    private fun openAudio() {
        mediaPlayer = MediaPlayer.create(
            this,
            R.raw.demo
        ) // replace 'your_audio_file' with the actual file name
        mediaPlayer?.isLooping = true
        mediaPlayer?.setVolume(
            SharedPreferencesManager.getValueVolume().toFloat(),
            SharedPreferencesManager.getValueVolume().toFloat()
        )// Set looping
        mediaPlayer?.start()
    }


    @SuppressLint("LaunchActivityFromNotification")
    private fun sendNotification() {
        try {
            val intent = Intent(
                MyApplication.getApplication().applicationContext,
                ResetServiceBroadcastReceiver::class.java
            )
            val pendingIntent = PendingIntent.getBroadcast(
                MyApplication.getApplication().applicationContext,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val context = MyApplication.getApplication().applicationContext
            val channelId = "channel_id"
            val channelName = "Channel Name"
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.icon_power)
                .setContentTitle("Hi, phone clapped")
                .setContentText("I'm here, do you see me ?")
                .setContentIntent(pendingIntent)
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
        } catch (e: Exception) {
            Log.d("LucTV", "sendNotification: ")
        }
    }

    private fun requestCamera() {
        val value = SharedPreferencesManager.getValueFlash()
        if (value != 0) {
            cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
            try {
                cameraId = cameraManager.cameraIdList[0]
                startFlashing(value)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }

    private fun startFlashing(value: Int) {
        flashJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                toggleFlashlight()
                delay(value.toLong())  // Nháy đèn mỗi giây
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
        if (SharedPreferencesManager.getVibrate()) {
            startVibrating()
        }
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
        val delay = 0
        val vibrate = 1000
        val sleep = 1000
        val start = 0
        val vibratePattern = longArrayOf(delay.toLong(), vibrate.toLong(), sleep.toLong())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(vibratePattern, start))
        } else {
            vibrator?.vibrate(vibratePattern, start)
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        detectorThread.start()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        resetDataService()
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

package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.home.views

import android.Manifest
import android.app.KeyguardManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.eskimo.findmyphone.locatemydevice.trackmymobile.R
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.ActivityFindPhoneLockScreenBinding

object AppNotificationManager {
    private const val TAG = "LucTV"

    private const val DefaultNotificationChannelId = "default"
    private const val DefaultNotificationChannelName = "Default"
    private const val DefaultNotificationChannelDesc = "Default notification channel"

    private const val LockscreenWidgetNotificationId = 20000

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel.
            val name = DefaultNotificationChannelName
            val descriptionText = DefaultNotificationChannelDesc
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(DefaultNotificationChannelId, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    private fun sendNotification(
        context: Context,
        notificationId: Int,
        notification: Notification,
    ) {
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            notify(notificationId, notification)
        }
    }

    private fun cancelNotification(context: Context, notificationId: Int) {
        with(NotificationManagerCompat.from(context)) {
            cancel(notificationId)
        }
    }

    fun sendLockscreenWidgetNotification(
        context: Context,
    ) {
        Log.d(TAG, "sendLockscreenWidgetNotification: ")
        val powerManager = context.getSystemService(PowerManager::class.java)
        val keyguardManager = context.getSystemService(KeyguardManager::class.java)

        if (!powerManager.isInteractive || (powerManager.isInteractive && keyguardManager.isKeyguardLocked)) {
            val fullScreenIntent =
                Intent(context, FindPhoneLockScreenActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            val fullScreenPendingIntent = PendingIntent.getActivity(
                context, 0,
                fullScreenIntent, PendingIntent.FLAG_IMMUTABLE
            )

            val notificationBuilder =
                NotificationCompat.Builder(context, DefaultNotificationChannelId)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setSound(null)
                    .setVibrate(longArrayOf(0))
                    .setFullScreenIntent(fullScreenPendingIntent, true)

            // Cancel old notification
            cancelNotification(context, LockscreenWidgetNotificationId)
            // Send new notification
            if (keyguardManager.inKeyguardRestrictedInputMode()) {
                sendNotification(
                    context,
                    LockscreenWidgetNotificationId,
                    notificationBuilder.build()
                )
            }
        }
    }
}
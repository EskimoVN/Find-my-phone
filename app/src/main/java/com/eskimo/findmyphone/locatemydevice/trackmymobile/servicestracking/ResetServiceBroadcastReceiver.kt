package com.eskimo.findmyphone.locatemydevice.trackmymobile.servicestracking

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ResetServiceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, AudioDetectService::class.java)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(333)
        context.stopService(serviceIntent)
        GlobalScope.launch {
            delay(1000)
            context.startService(serviceIntent)
        }
    }
}
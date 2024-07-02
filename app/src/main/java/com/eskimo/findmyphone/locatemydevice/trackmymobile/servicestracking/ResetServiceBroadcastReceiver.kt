package com.eskimo.findmyphone.locatemydevice.trackmymobile.servicestracking

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.eskimo.findmyphone.locatemydevice.trackmymobile.sdptracking.ServiceManager

class ResetServiceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
//        val serviceIntent = Intent(context, DSPDetectService::class.java)
//        val notificationManager =
//            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.cancel(333)
//        context.stopService(serviceIntent)
//        GlobalScope.launch {
//            delay(300)
//            context.startService(serviceIntent)
//        }
        ServiceManager.dspDetectService?.resetDataService()
    }
}
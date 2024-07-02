package com.eskimo.findmyphone.locatemydevice.trackmymobile.servicestracking

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.eskimo.findmyphone.locatemydevice.trackmymobile.sdptracking.DSPDetectService
import com.eskimo.findmyphone.locatemydevice.trackmymobile.sdptracking.ServiceManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PowerVolumeButtonReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        ServiceManager.dspDetectService?.resetDataService()
    }
}
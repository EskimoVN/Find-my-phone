package com.tunv.admob.common

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

object AdSDK {
    fun initialize(context: Context, listTestDevice: List<String>, isDebug: Boolean) {
        // Initialize AdMob SDK
        if (isDebug) {
            val requestConfiguration = if (listTestDevice.isNotEmpty()) {
                listTestDevice.toMutableList().add(AdRequest.DEVICE_ID_EMULATOR)
                RequestConfiguration.Builder()
                    .setTestDeviceIds(listTestDevice).build()
            } else {
                RequestConfiguration.Builder()
                    .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR)).build()
            }
            MobileAds.setRequestConfiguration(requestConfiguration)
        }
        MobileAds.initialize(context)
    }
}
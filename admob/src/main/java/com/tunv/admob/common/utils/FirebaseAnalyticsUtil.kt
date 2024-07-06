package com.tunv.admob.common.utils

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.AdValue
import com.google.firebase.analytics.FirebaseAnalytics

object FirebaseAnalyticsUtil {
    private const val TAG = "FirebaseAnalyticsUtil"
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    fun init(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    }

    fun logPaidAdImpression(context: Context, params: AdValue) {
        val bundle = Bundle().apply {
            putString("ad_unit_id", params.currencyCode)
            putLong("ad_network", params.valueMicros)
            putString("ad_platform", params.precisionType.toString())
        }
        FirebaseAnalytics.getInstance(context).logEvent("paid_ad_impression", bundle)
    }

    fun logEventTracking(eventName: String, bundle: Bundle) {
        firebaseAnalytics.logEvent(eventName, bundle)
    }
}
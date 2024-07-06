package com.tunv.admob.common.openAd

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.tunv.admob.R
import com.tunv.admob.common.interstitialAd.InterstitialAdUtil
import com.tunv.admob.common.openAd.OpenAdConfig.isOpenAdLoading
import com.tunv.admob.common.openAd.OpenAdConfig.isOpenAdShowing
import com.tunv.admob.common.openAd.OpenAdConfig.isOpenAdStop
import com.tunv.admob.common.utils.FirebaseAnalyticsUtil
import com.tunv.admob.common.utils.LoadingUtils
import com.tunv.admob.common.utils.isNetworkAvailable
import com.tunv.admob.common.utils.showLog

class OpenAdManager(
    application: Application,
    private val openAdId: String,
    private val loadingLayout: Int = R.layout.openad_loading_layout
) : LifecycleObserver, Application.ActivityLifecycleCallbacks {
    private var openAd: AppOpenAd? = null
    private var currentActivity: Activity? = null

    init {
        try {
            application.registerActivityLifecycleCallbacks(this)
            ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        } catch (e: Exception) {
            OpenAdManager::class.toString().showLog(e.message.toString())
        }
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        currentActivity = p0
    }

    override fun onActivityStarted(p0: Activity) {
        currentActivity = p0
    }

    override fun onActivityResumed(p0: Activity) {
        currentActivity = p0
    }

    override fun onActivityPaused(p0: Activity) {

    }

    override fun onActivityStopped(p0: Activity) {

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(p0: Activity) {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onAppForegrounded() {
        if (isOpenAdStop) {
            "tunv".showLog("OpenAd is stopped")
            isOpenAdStop = false
            return
        }
        "tunv".showLog("OpenAd is started")
        currentActivity?.let {
            if (OpenAdConfig.isAdEnable() && !isOpenAdStop) {
                fetchAd()
            }
        }
    }

    private fun fetchAd() {
        currentActivity?.let {
            if (it.isNetworkAvailable()) {
                if (!InterstitialAdUtil.isInterstitialShowing && !isOpenAdLoading && !isOpenAdShowing) {
                    isOpenAdLoading = true
                    LoadingUtils.showAdLoadingScreen(it, loadingLayout)
                    val loadCallback: AppOpenAd.AppOpenAdLoadCallback =
                        object : AppOpenAd.AppOpenAdLoadCallback() {
                            override fun onAdLoaded(ad: AppOpenAd) {
                                isOpenAdLoading = false
                                openAd = ad
                                currentActivity?.let { activity ->
                                    openAd?.setOnPaidEventListener { paid ->
                                        FirebaseAnalyticsUtil.logPaidAdImpression(
                                            activity,
                                            paid
                                        )
                                    }
                                }
                                showOpenAd()
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                isOpenAdLoading = false
                                LoadingUtils.dismissScreen()
                            }
                        }
                    val request: AdRequest = AdRequest.Builder().build()
                    AppOpenAd.load(
                        it,
                        openAdId,
                        request,
                        loadCallback
                    )
                }
            }

        }
    }

    private fun showOpenAd() {
        if (openAd != null) {
            val fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    openAd = null
                    LoadingUtils.dismissScreen()
                    isOpenAdShowing = false

                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    isOpenAdShowing = false
                    LoadingUtils.dismissScreen()
                }

                override fun onAdShowedFullScreenContent() {
                    isOpenAdShowing = true

                }
            }
            openAd?.fullScreenContentCallback = fullScreenContentCallback
            currentActivity?.let {
                try {
                    openAd?.show(it)
                } catch (ex: Exception) {
                    Log.d("", ex.message.toString())
                }
            }
        } else {
            LoadingUtils.dismissScreen()
        }
    }
}


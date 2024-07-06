package com.tunv.admob.common.openAd

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.tunv.admob.common.callback.AdCallBack
import com.tunv.admob.common.interstitialAd.InterstitialAdUtil
import com.tunv.admob.common.utils.FirebaseAnalyticsUtil
import com.tunv.admob.common.utils.isNetworkAvailable

object OpenAdConfig {
    private var appOpenAd: AppOpenAd? = null
    private var isOpenAdAllowed: Boolean = false
    var isOpenAdStop: Boolean = false
    var isOpenAdLoading: Boolean = false
    var isOpenAdShowing: Boolean = false

    fun enableResumeAd() {
        isOpenAdAllowed = true
    }

    fun disableResumeAd() {
        isOpenAdAllowed = false
    }

    fun isAdEnable() = isOpenAdAllowed

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null
    }

    fun loadAdsAndShow(activity: Activity, adCallBack: AdCallBack?, openAdId: String) {
        if (activity.isNetworkAvailable()) {
            if (!InterstitialAdUtil.isInterstitialShowing && !isOpenAdLoading && !isOpenAdShowing) {
                isOpenAdLoading = true
                val loadCallback: AppOpenAd.AppOpenAdLoadCallback =
                    object : AppOpenAd.AppOpenAdLoadCallback() {
                        override fun onAdLoaded(ad: AppOpenAd) {
                            ad.setOnPaidEventListener { paid ->
                                FirebaseAnalyticsUtil.logPaidAdImpression(activity, paid)
                            }
                            isOpenAdLoading = false
                            adCallBack?.onAdLoaded()
                            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {
                                    isOpenAdShowing = false
                                    isOpenAdStop = false
                                    adCallBack?.onAdDismiss()
                                }

                                override fun onAdShowedFullScreenContent() {
                                    super.onAdShowedFullScreenContent()
                                    isOpenAdShowing = true
                                    isOpenAdStop = true
                                }

                                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                    isOpenAdShowing = false
                                    isOpenAdStop = false
                                    adCallBack?.onAdFailToShow(Exception(adError.message))
                                }
                            }
                            ad.show(activity)
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            isOpenAdLoading = false
                            adCallBack?.onAdFailToLoad(Exception(loadAdError.message))
                        }
                    }
                val request: AdRequest = AdRequest.Builder().build()
                AppOpenAd.load(
                    activity,
                    openAdId,
                    request,
                    loadCallback
                )
            }


        }
    }

}
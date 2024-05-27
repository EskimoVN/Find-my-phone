package com.tunv.admob.common.interstitialAd

import android.app.Activity
import android.content.Context
import com.tunv.admob.common.callback.AdCallBack
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.tunv.admob.common.openAd.OpenAdConfig
import com.tunv.admob.common.utils.isNetworkAvailable


object InterstitialAdUtil {
    var isInterstitialShowing: Boolean = false

    private var interstitialAd: InterstitialAd? = null

    fun loadInterstitial(context: Context, adId: String, adCallBack: AdCallBack? = null) {
        if (context.isNetworkAvailable()) {
            if (interstitialAd == null) {
                InterstitialAd.load(context, adId,
                    AdRequest.Builder().build(),
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(p0: InterstitialAd) {
                            super.onAdLoaded(p0)
                            interstitialAd = p0
                            adCallBack?.onAdLoaded()
                        }

                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            interstitialAd = null
                            adCallBack?.onAdFailToLoad(Exception(p0.message))
                        }

                    })
            }
        } else {
            adCallBack?.onAdFailToLoad(Exception("No internet connection"))
        }
    }

    fun showInterstitial(activity: Activity, adCallBack: AdCallBack) {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        isInterstitialShowing = false
                        interstitialAd = null
                        adCallBack.onAdDismiss()
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        isInterstitialShowing = false
                        adCallBack.onAdFailToShow(Exception(p0.message))
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        // tắt quảng cáo open khi click vào quảng cáo interstitial
                        OpenAdConfig.isOpenAdStop = true
                        adCallBack.onAdClick()
                    }

                    override fun onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent()
                        isInterstitialShowing = true
                        adCallBack.onAdShown()
                    }
                }
            interstitialAd?.show(activity)
        } else {
            adCallBack.onAdFailToShow(Exception("Interstitial ad is not loaded yet."))
        }
    }

    fun showInterstitial(
        activity: Activity,
        adId: String,
        reloadOnDismiss: Boolean,
        adCallBack: AdCallBack
    ) {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        isInterstitialShowing = false
                        interstitialAd = null
                        // load lại interstitial khi tắt quảng cáo
                        if (reloadOnDismiss) {
                            loadInterstitial(activity, adId)
                        }
                        adCallBack.onAdDismiss()
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        isInterstitialShowing = false
                        adCallBack.onAdFailToShow(Exception(p0.message))
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        OpenAdConfig.isOpenAdStop = true
                        adCallBack.onAdClick()
                    }

                    override fun onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent()
                        isInterstitialShowing = true
                        adCallBack.onAdShown()
                    }
                }
            interstitialAd?.show(activity)
        } else {
            loadInterstitial(activity, adId, object : AdCallBack() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    showInterstitial(activity, adId, reloadOnDismiss, adCallBack)
                }

                override fun onAdFailToLoad(error: Exception) {
                    super.onAdFailToLoad(error)
                    adCallBack.onAdFailToLoad(error)
                }
            })
        }
    }

}
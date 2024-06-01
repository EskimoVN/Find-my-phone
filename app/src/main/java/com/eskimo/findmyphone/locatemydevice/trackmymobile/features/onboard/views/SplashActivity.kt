package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.onboard.views

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.eskimo.findmyphone.locatemydevice.trackmymobile.BuildConfig
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.MyApplication
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.SharedPreferencesManager
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.ui.BaseActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.ActivitySplashBinding
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.home.views.HomeActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.language.views.LanguageActivity
import com.google.android.gms.ads.nativead.NativeAd
import com.tunv.admob.common.bannerAd.BannerAdUtil
import com.tunv.admob.common.callback.AdCallBack
import com.tunv.admob.common.interstitialAd.InterstitialAdUtil
import com.tunv.admob.common.nativeAds.NativeAdsUtil
import com.tunv.admob.common.openAd.OpenAdConfig
import com.tunv.admob.common.utils.isNetworkAvailable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        OpenAdConfig.disableResumeAd()
        if (this.isNetworkAvailable()) {
            loadAd()
        } else {
            lifecycleScope.launch {
                delay(2000L)
                startMain()
            }
            startMain()
        }
        loadAd()
        preloadNativeAd()
    }

    private fun preloadNativeAd() {
        if (MyApplication.getApplication().nativeLanguageConfig && this.isNetworkAvailable()) {
            NativeAdsUtil.loadNativeAd(
                nativeId = BuildConfig.ad_native_language,
                context = this,
                adListener = object : AdCallBack() {
                    override fun onNativeAdLoad(nativeAd: NativeAd) {
                        super.onNativeAdLoad(nativeAd)
                        MyApplication.getApplication().getStorageCommon().nativeAdLanguage.setValue(
                            nativeAd
                        )
                    }
                })
        }
    }

    private fun loadAd() {
        BannerAdUtil.showBanner(
            binding.bannerView,
            BuildConfig.ad_banner_splash,
            this,
            object : AdCallBack() {

            })
        // sau 15s mà không load xong thì vào main luôn
        var isInterstitialAdLoaded = false
        lifecycleScope.launch {
            delay(10000L)
            if (!isInterstitialAdLoaded) {
                startMain()
            }
        }
        InterstitialAdUtil.loadInterstitial(context = this,
            adId = "sfds",
            adCallBack = object : AdCallBack() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    isInterstitialAdLoaded = true
                    InterstitialAdUtil.showInterstitial(
                        activity = this@SplashActivity,
                        adCallBack = object : AdCallBack() {
                            override fun onAdDismiss() {
                                super.onAdDismiss()
                                startMain()
                            }

                            override fun onAdFailToShow(error: Exception) {
                                super.onAdFailToShow(error)
                                startMain()
                            }
                        }
                    )
                }

                override fun onAdFailToLoad(error: Exception) {
                    super.onAdFailToLoad(error)
                    OpenAdConfig.loadAdsAndShow(
                        activity = this@SplashActivity,
                        openAdId = BuildConfig.ads_open_app,
                        adCallBack = object : AdCallBack() {
                            override fun onAdLoaded() {
                                super.onAdLoaded()
                                isInterstitialAdLoaded = true
                            }

                            override fun onAdDismiss() {
                                super.onAdDismiss()
                                startMain()
                            }

                            override fun onAdFailToShow(error: Exception) {
                                super.onAdFailToShow(error)
                                startMain()
                            }
                        }
                    )
                }

            }
        )
    }

    private fun startMain() {
        if (!SharedPreferencesManager.getFirstOpen()) {
            startActivity(
                Intent(
                    this@SplashActivity,
                    HomeActivity::class.java
                )
            )
        } else {
            startActivity(
                Intent(
                    this@SplashActivity,
                    LanguageActivity::class.java
                )
            )
        }
    }
}
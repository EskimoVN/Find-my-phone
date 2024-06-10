package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.setting.views

import android.content.Intent
import android.os.Bundle
import com.eskimo.findmyphone.locatemydevice.trackmymobile.BuildConfig
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.Constant
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.MyApplication
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.SharedPreferencesManager
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.openLink
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.setOnSafeClickListener
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.ui.BaseActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.ActivitySettingBinding
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.language.views.LanguageActivity
import com.google.android.gms.ads.nativead.NativeAd
import com.tunv.admob.common.bannerAd.BannerAdUtil
import com.tunv.admob.common.callback.AdCallBack
import com.tunv.admob.common.nativeAds.NativeAdsUtil
import com.tunv.admob.common.openAd.OpenAdConfig
import com.tunv.admob.common.utils.isNetworkAvailable

class SettingActivity : BaseActivity() {

    lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        preloadNativeAd()
        setupAds()
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

    private fun setupAds() {
        OpenAdConfig.enableResumeAd()
//        if (this.isNetworkAvailable() && MyApplication.getApplication().nativeLanguageConfig) {
//            MyApplication.getApplication().getStorageCommon().nativeAdLanguage.observe(this)
//            {
//                if (it != null) {
//                    NativeAdsUtil.populateNativeAd(
//                        this,
//                        binding.frAds,
//                        it,
//                        com.tunv.admob.R.layout.custom_native_admod_medium,
//                        adListener = object : AdCallBack() {
//                            override fun onAdClick() {
//                                super.onAdClick()
//                                reloadNativeAd()
//                            }
//                        })
//                }
//
//            }
//        } else {
//            binding.frAds.gone()
//        }
        BannerAdUtil.showBanner(
            MyApplication.getApplication().bannerConfig,
            binding.bannerView,
            BuildConfig.ad_banner,
            this,
            object : AdCallBack() {

            })
    }

    private fun reloadNativeAd() {
        if (MyApplication.getApplication().nativeSettingConfig && this.isNetworkAvailable()) {
            NativeAdsUtil.loadNativeAd(
                nativeId = BuildConfig.ad_native_language,
                context = this,
                adListener = object : AdCallBack() {
                    override fun onNativeAdLoad(nativeAd: NativeAd) {
                        super.onNativeAdLoad(nativeAd)
                        MyApplication.getApplication().getStorageCommon().nativeAdSetting.setValue(
                            nativeAd
                        )
                    }
                })
        }
    }

    private fun setupViews() {
        binding.buttonLanguage.setOnSafeClickListener {
            val intent = Intent(this, LanguageActivity::class.java)
            intent.putExtra(
                SharedPreferencesManager.KEY_OPEN_SETTING,
                true
            )
            startActivity(intent)
        }
        binding.buttonBack.setOnSafeClickListener {
            finish()
        }
        binding.buttonTerm.setOnSafeClickListener {
            openLink(Constant.URL_TERM)
        }
        binding.buttonPolicy.setOnSafeClickListener {
            openLink(Constant.URL_POLICY)
        }
    }
}
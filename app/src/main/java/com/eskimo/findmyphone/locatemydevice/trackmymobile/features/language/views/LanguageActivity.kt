package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.language.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.BuildConfig
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.MyApplication
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.SharedPreferencesManager
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.gone
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.setOnSafeClickListener
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.ActivityLanguageBinding
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.language.datas.Language
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.language.datas.LocaleUtils
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.onboard.views.OnBoardActivity
import com.google.android.gms.ads.nativead.NativeAd
import com.tunv.admob.common.callback.AdCallBack
import com.tunv.admob.common.nativeAds.NativeAdsUtil
import com.tunv.admob.common.openAd.OpenAdConfig
import com.tunv.admob.common.utils.isNetworkAvailable

class LanguageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLanguageBinding
    private lateinit var adapter: LanguageAdapter
    private lateinit var languages: List<Language>
    private var languageSelected: String = SharedPreferencesManager.getAppLanguage()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAdapter()
        setupViews()
        setupAd()
        preloadNativeAd()
    }

    private fun preloadNativeAd() {
        if (MyApplication.getApplication().nativeOnboardingConfig) {
            NativeAdsUtil.loadNativeAd(
                nativeId = BuildConfig.ad_native_onboarding,
                context = this,
                adListener = object : AdCallBack() {
                    override fun onNativeAdLoad(nativeAd: NativeAd) {
                        super.onNativeAdLoad(nativeAd)
                        MyApplication.getApplication()
                            .getStorageCommon().nativeAdOnboarding1.setValue(
                                nativeAd
                            )
                    }
                })
        }
    }

    private fun setupAd() {
        OpenAdConfig.enableResumeAd()
        if (this.isNetworkAvailable() && MyApplication.getApplication().nativeLanguageConfig) {
            MyApplication.getApplication().getStorageCommon().nativeAdLanguage.observe(this)
            {
                if (it != null) {
                    NativeAdsUtil.populateNativeAd(
                        this,
                        binding.frAds,
                        it,
                        com.tunv.admob.R.layout.custom_native_admod_medium,
                        adListener = object : AdCallBack() {
                            override fun onAdClick() {
                                super.onAdClick()
                                reloadNativeAd()
                            }
                        })
                }

            }
        }else{
            binding.frAds.gone()
        }
    }

    private fun reloadNativeAd() {
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

    private fun setupViews() {
        binding.buttonNext.setOnSafeClickListener {
            startActivity(Intent(this, OnBoardActivity::class.java))
        }
    }

    private fun setupAdapter() {
        languages = LocaleUtils.listLanguage
        adapter = LanguageAdapter(this, languages) {
            languageSelected = it.code
        }
        binding.recyclerView.adapter = adapter
    }
}
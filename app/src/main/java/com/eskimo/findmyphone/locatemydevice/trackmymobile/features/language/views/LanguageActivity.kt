package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.language.views

import android.content.Intent
import android.os.Bundle
import com.eskimo.findmyphone.locatemydevice.trackmymobile.BuildConfig
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.Constant
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.MyApplication
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.SharedPreferencesManager
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.gone
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.setOnSafeClickListener
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.visible
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.ui.BaseActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.ActivityLanguageBinding
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.language.datas.Language
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.language.datas.LocaleUtils
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.onboard.views.OnBoardActivity
import com.google.android.gms.ads.nativead.NativeAd
import com.tunv.admob.common.callback.AdCallBack
import com.tunv.admob.common.nativeAds.NativeAdsUtil
import com.tunv.admob.common.openAd.OpenAdConfig
import com.tunv.admob.common.utils.isNetworkAvailable

class LanguageActivity : BaseActivity() {
    private lateinit var binding: ActivityLanguageBinding
    private lateinit var adapter: LanguageAdapter
    private lateinit var languages: List<Language>
    private var languageSelected: String = SharedPreferencesManager.getAppLanguage()
    private var openFromSetting = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        openFromSetting = intent.getBooleanExtra(SharedPreferencesManager.KEY_OPEN_SETTING, false)
        setupAdapter()
        setupViews()
        setupAds()
        preloadNativeAd()
    }

    private fun preloadNativeAd() {
        if (MyApplication.getApplication().nativeOnboardingConfig && !openFromSetting) {
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

    private fun setupAds() {
        OpenAdConfig.enableResumeAd()
        if (this.isNetworkAvailable() && MyApplication.getApplication().nativeLanguageConfig) {
            if (SharedPreferencesManager.getAppLanguage().isEmpty()) {
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
            } else {
                MyApplication.getApplication().getStorageCommon().nativeAdLanguage2.observe(this)
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
            }

        } else {
            binding.frAds.gone()
        }
    }

    private fun reloadNativeAd() {
        if (SharedPreferencesManager.getAppLanguage().isEmpty()) {
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
        } else {
            NativeAdsUtil.loadNativeAd(
                nativeId = BuildConfig.ad_native_language_2,
                context = this,
                adListener = object : AdCallBack() {
                    override fun onNativeAdLoad(nativeAd: NativeAd) {
                        super.onNativeAdLoad(nativeAd)
                        MyApplication.getApplication()
                            .getStorageCommon().nativeAdLanguage2.setValue(
                                nativeAd
                            )
                    }
                })
        }

    }

    private fun setupViews() {
        if (openFromSetting) binding.buttonBack.visible() else binding.buttonBack.gone()
        binding.buttonNext.setOnSafeClickListener {
            if (!openFromSetting) {
                if (SharedPreferencesManager.getAppLanguage().isEmpty()) {
                    if (languageSelected.isEmpty()) {
                        languageSelected = LocaleUtils.listLanguage[0].code
                    }
                    SharedPreferencesManager.setAppLanguage(languageSelected)
                    val intent = Intent(this, LanguageActivity::class.java)
                    intent.putExtra(
                        Constant.KEY_DATA, languageSelected
                    )
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                } else {
                    val intent = Intent(this, OnBoardActivity::class.java)
                    startActivity(intent)
                }

            }
            SharedPreferencesManager.setAppLanguage(languageSelected)
            finish()
        }
        binding.buttonBack.setOnSafeClickListener {
            finish()
        }
    }

    private fun setupAdapter() {
        languages = LocaleUtils.listLanguage
        adapter = LanguageAdapter(this, languages) { language ->
            if (languageSelected.isEmpty()) {
                languageSelected = language.code
                SharedPreferencesManager.setAppLanguage(languageSelected)
                val intent = Intent(this, LanguageActivity::class.java)
                intent.putExtra(
                    Constant.KEY_DATA, languageSelected
                )
                startActivity(intent)
                overridePendingTransition(0, 0)
            } else {
                languageSelected = language.code
            }
        }
        binding.recyclerView.adapter = adapter
    }
}
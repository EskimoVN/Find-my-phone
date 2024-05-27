package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.language.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.BuildConfig
import com.eskimo.findmyphone.locatemydevice.trackmymobile.R
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.MyApplication
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.SharedPreferencesManager
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.setOnSafeClickListener
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.ActivityLanguageBinding
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.home.views.HomeActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.language.datas.Language
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.language.datas.LocaleUtils
import com.google.android.gms.ads.nativead.NativeAd
import com.tunv.admob.common.callback.AdCallBack
import com.tunv.admob.common.nativeAds.NativeAdsUtil
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
    }

    private fun setupAd() {
        if (this.isNetworkAvailable()) {
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
                                preloadNativeAd()
                            }
                        })
                }

            }
        }
    }

    private fun preloadNativeAd() {
        NativeAdsUtil.loadNativeAd(
            nativeId = BuildConfig.ad_native_language,
            context = this,
            isAdAllowed = true,
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
            startActivity(Intent(this, HomeActivity::class.java))
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
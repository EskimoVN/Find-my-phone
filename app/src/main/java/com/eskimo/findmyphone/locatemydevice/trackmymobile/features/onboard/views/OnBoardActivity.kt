package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.onboard.views

import android.content.Intent
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.eskimo.findmyphone.locatemydevice.trackmymobile.BuildConfig
import com.eskimo.findmyphone.locatemydevice.trackmymobile.R
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.MyApplication
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.SharedPreferencesManager
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.invisible
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.setOnSafeClickListener
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.ui.BaseActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.ActivityOnBoardBinding
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.home.views.HomeActivity
import com.google.android.gms.ads.nativead.NativeAd
import com.tunv.admob.common.callback.AdCallBack
import com.tunv.admob.common.nativeAds.NativeAdsUtil
import com.tunv.admob.common.utils.isNetworkAvailable

class OnBoardActivity : BaseActivity() {
    private lateinit var binding: ActivityOnBoardBinding
    var currentPosition = TAB_INDEX_INTRODUCE

    companion object {
        const val TAB_INDEX_INTRODUCE = 0
        const val TAB_INDEX_INTRODUCE2 = 1
        const val TAB_INDEX_INTRODUCE3 = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupImage()
        detectView()
        setupAds()
        preloadNativeAd()
    }

    private fun preloadNativeAd() {
        if (MyApplication.getApplication().nativeHomeConfig && this.isNetworkAvailable()) {
            NativeAdsUtil.loadNativeAd(
                nativeId = BuildConfig.ad_native_home,
                context = this,
                adListener = object : AdCallBack() {
                    override fun onNativeAdLoad(nativeAd: NativeAd) {
                        super.onNativeAdLoad(nativeAd)
                        MyApplication.getApplication().getStorageCommon().nativeAdHome.setValue(
                            nativeAd
                        )
                    }
                })
        }
    }

    private fun setupAds() {
        if (MyApplication.getApplication().nativeOnboardingConfig && this.isNetworkAvailable()) {
            showPreNative()
        } else {
            binding.frAds.invisible()
        }
    }

    private fun showPreNative() {
        if (this.isNetworkAvailable() && MyApplication.getApplication().nativeOnboardingConfig) {
            MyApplication.getApplication().getStorageCommon().nativeAdOnboarding1.observe(this)
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
            binding.frAds.invisible()
        }
    }

    private fun reloadNativeAd() {
        NativeAdsUtil.loadNativeAd(
            nativeId = BuildConfig.ad_native_onboarding,
            context = this,
            adListener = object : AdCallBack() {
                override fun onNativeAdLoad(nativeAd: NativeAd) {
                    super.onNativeAdLoad(nativeAd)
                    MyApplication.getApplication().getStorageCommon().nativeAdOnboarding1.setValue(
                        nativeAd
                    )
                }
            })
    }

    private fun detectView() {
        binding.buttonContinue.setOnSafeClickListener() {
            when (currentPosition) {
                TAB_INDEX_INTRODUCE -> {
                    binding.viewPagerSlideIntro.currentItem = TAB_INDEX_INTRODUCE2
                }

                TAB_INDEX_INTRODUCE2 -> {
                    binding.viewPagerSlideIntro.currentItem = TAB_INDEX_INTRODUCE3
                }

                else -> {
                    SharedPreferencesManager.setFirstOpen(false)
                    startActivity(Intent(this, HomeActivity::class.java))
                    this.finish()
                }
            }
        }
    }

    private fun setupImage() {
        val listImageIntro = listOf(
            R.drawable.image_onboard_1,
            R.drawable.image_onboard_2,
            R.drawable.image_onboard_3,
        )
        val listTitle = listOf(
            getString(R.string.text_title_onboard_1),
            getString(R.string.text_title_onboard_2),
            getString(R.string.text_title_onboard_3),
        )

        val listContent = listOf(
            getString(R.string.text_content_splash),
            getString(R.string.text_content_splash),
            getString(R.string.text_content_splash),
        )
        binding.tabLayout.setupWithViewPager(binding.viewPagerSlideIntro)
        binding.viewPagerSlideIntro.adapter =
            SlideImageIntroAdapter(listImageIntro, listTitle, listContent)
        binding.viewPagerSlideIntro.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int,
            ) {
            }

            override fun onPageSelected(position: Int) {
                currentPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }
}
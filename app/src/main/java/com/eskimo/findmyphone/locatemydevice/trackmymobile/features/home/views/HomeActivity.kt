package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.home.views

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.eskimo.findmyphone.locatemydevice.trackmymobile.BuildConfig
import com.eskimo.findmyphone.locatemydevice.trackmymobile.R
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.MyApplication
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.ui.BaseActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.ActivityHomeBinding
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.onboard.views.ExitDialog
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.tabs.TabLayout
import com.tunv.admob.common.bannerAd.BannerAdUtil
import com.tunv.admob.common.callback.AdCallBack
import com.tunv.admob.common.nativeAds.NativeAdsUtil
import com.tunv.admob.common.openAd.OpenAdConfig
import com.tunv.admob.common.utils.isNetworkAvailable


class HomeActivity : BaseActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var doubleBackToExitPressed: Boolean = false
    private lateinit var homePagerAdapter: HomePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewPager()
        setupTabLayout()
        requestNotification()
        preloadNativeAd()
    }

    private fun preloadNativeAd() {
        if (MyApplication.getApplication().nativeExitDialog && this.isNetworkAvailable()) {
            NativeAdsUtil.loadNativeAd(
                nativeId = BuildConfig.ad_native_exit_dialog,
                context = this,
                adListener = object : AdCallBack() {
                    override fun onNativeAdLoad(nativeAd: NativeAd) {
                        super.onNativeAdLoad(nativeAd)
                        MyApplication.getApplication()
                            .getStorageCommon().nativeAdExitDialog.setValue(
                                nativeAd
                            )
                    }
                })
        }

    }
//    private fun preloadNativeAd() {
//        if (MyApplication.getApplication().nativeSettingConfig && this.isNetworkAvailable()) {
//            NativeAdsUtil.loadNativeAd(
//                nativeId = BuildConfig.ad_native_language,
//                context = this,
//                adListener = object : AdCallBack() {
//                    override fun onNativeAdLoad(nativeAd: NativeAd) {
//                        super.onNativeAdLoad(nativeAd)
//                        MyApplication.getApplication().getStorageCommon().nativeAdSetting.setValue(
//                            nativeAd
//                        )
//                    }
//                })
//        }
//    }

    override fun onBackPressed() {
        if (doubleBackToExitPressed) {
            ExitDialog(this@HomeActivity).show()
        }

        doubleBackToExitPressed = true
        Toast.makeText(
            this@HomeActivity,
            getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()

        Handler(Looper.getMainLooper()).postDelayed(
            { doubleBackToExitPressed = false }, 2000
        )
    }

    override fun onResume() {
        super.onResume()
        setupAds()
    }

    private fun setupAds() {
        OpenAdConfig.enableResumeAd()
        BannerAdUtil.showBanner(
            MyApplication.getApplication().bannerConfig,
            binding.bannerView,
            BuildConfig.ad_banner,
            this,
            object : AdCallBack() {

            })
    }

    private fun requestCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private val requestCameraLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                //   shortToast(R.string.text_please_access_permission_notification)
            }
        }


    private val requestPermissionNotificationLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                //   shortToast(R.string.text_please_access_permission_notification)
            } else {
            }
        }

    private fun requestNotification() {
        AppNotificationManager.createNotificationChannels(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionNotificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
            }
        }
    }

    private fun setupViewPager() {
        binding.viewPagerHome.setSwipingEnabled(false, true)
        homePagerAdapter = HomePagerAdapter(supportFragmentManager)
        binding.viewPagerHome.adapter = homePagerAdapter
        binding.tabLayoutHome.setupWithViewPager(binding.viewPagerHome)
        binding.viewPagerHome.offscreenPageLimit = homePagerAdapter.count - 1
    }

    private fun setupTabLayout() {
        binding.tabLayoutHome.getTabAt(HomePagerAdapter.TAB_INDEX_FIND_PHONE)
            ?.setCustomView(R.layout.item_tab_layout_find_phone)
        binding.tabLayoutHome.getTabAt(HomePagerAdapter.TAB_INDEX_ANTI_THEFT)
            ?.setCustomView(R.layout.item_tab_layout_anti_theft)
        binding.tabLayoutHome.getTabAt(HomePagerAdapter.TAB_INDEX_LOST_PHONE)
            ?.setCustomView(R.layout.item_tab_layout_lost_phone)
        binding.tabLayoutHome.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab) {
//                    binding.tabLayoutHome.getTabAt(HomePagerAdapter.TAB_INDEX_INSIGHT) -> {
//                        logEvent(LogEventName.HOME_SCR_CLICK_TAB_INSIGHT.eventName)
//                    }
//                    binding.tabLayoutHome.getTabAt(HomePagerAdapter.TAB_INDEX_PROFILE) -> {
//                        logEvent(LogEventName.HOME_SCR_CLICK_TAB_PROFILE.eventName)
//                    }
                }
                val textView: TextView = tab!!.customView!!.findViewById(R.id.text_view_tab)
                textView.typeface = ResourcesCompat.getFont(
                    this@HomeActivity,
                    R.font.poppins_bold
                )
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val textView: TextView = tab!!.customView!!.findViewById(R.id.text_view_tab)
                textView.typeface = ResourcesCompat.getFont(
                    this@HomeActivity,
                    R.font.poppins_regular
                )
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }
}
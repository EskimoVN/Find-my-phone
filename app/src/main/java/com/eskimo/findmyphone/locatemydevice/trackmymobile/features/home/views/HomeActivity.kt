package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.home.views

import android.os.Bundle
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.eskimo.findmyphone.locatemydevice.trackmymobile.R
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.ui.BaseActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.ActivityHomeBinding
import com.google.android.material.tabs.TabLayout


class HomeActivity : BaseActivity() {
    private lateinit var binding : ActivityHomeBinding

    private lateinit var homePagerAdapter: HomePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewPager()
        setupTabLayout()
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
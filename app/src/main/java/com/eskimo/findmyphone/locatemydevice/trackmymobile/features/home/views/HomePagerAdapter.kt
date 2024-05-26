package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.home.views

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.eskimo.findmyphone.locatemydevice.trackmymobile.R
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.MyApplication
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.view.FindPhoneFragment

class HomePagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    companion object {
        private const val TAB_COUNT = 3

        const val TAB_INDEX_FIND_PHONE = 0
        const val TAB_INDEX_ANTI_THEFT = 1
        const val TAB_INDEX_LOST_PHONE = 2
    }

    private val context = MyApplication.getApplication()

    override fun getItem(position: Int): Fragment = when (position) {
        TAB_INDEX_FIND_PHONE -> FindPhoneFragment()
        TAB_INDEX_ANTI_THEFT -> Fragment()
        TAB_INDEX_LOST_PHONE -> Fragment()
        else -> Fragment()
    }

    override fun getPageTitle(position: Int): CharSequence? = when (position) {
        TAB_INDEX_FIND_PHONE -> context.getString(R.string.text_tab_title_find_phone)
        TAB_INDEX_ANTI_THEFT -> context.getString(R.string.text_tab_tittle_anti_theft)
        TAB_INDEX_LOST_PHONE -> context.getString(R.string.text_tab_tittle_lost_phone)
        else -> null
    }

    override fun getCount() = TAB_COUNT
}
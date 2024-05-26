package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.onboard.views

import android.content.Intent
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.eskimo.findmyphone.locatemydevice.trackmymobile.R
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.SharedPreferencesManager
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.setOnSafeClickListener
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.ui.BaseActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.ActivityOnBoardBinding
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.home.views.HomeActivity

class OnBoardActivity :BaseActivity() {
    private lateinit var binding : ActivityOnBoardBinding
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
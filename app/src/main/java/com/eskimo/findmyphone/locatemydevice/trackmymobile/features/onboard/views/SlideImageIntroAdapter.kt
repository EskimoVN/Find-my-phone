package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.onboard.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.viewpager.widget.PagerAdapter
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.ItemViewPagerIntroBinding

class SlideImageIntroAdapter(
    private val listImage: List<Int>,
    private val listTitle: List<String>,
    private val listContent: List<String>
) : PagerAdapter() {
    override fun getCount() = listImage.size
    override fun isViewFromObject(view: View, `object`: Any) = view == `object`
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = ItemViewPagerIntroBinding.inflate(
            LayoutInflater.from(container.context), container, false
        )
        binding.imageViewSlideIntro.setImageResource(listImage[position])
        if (listContent[position].isNotEmpty()) {
            binding.textViewContent.text = listContent[position]
        }
        binding.textViewTittle.text = listTitle[position]
        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

}
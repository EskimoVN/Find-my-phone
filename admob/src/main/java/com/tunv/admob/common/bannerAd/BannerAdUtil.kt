package com.tunv.admob.common.bannerAd

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.tunv.admob.R
import com.tunv.admob.common.callback.AdCallBack
import com.tunv.admob.common.openAd.OpenAdConfig
import com.tunv.admob.common.utils.isNetworkAvailable

object BannerAdUtil {
    fun showBanner(
        isEnable: Boolean = true,
        adContainer: ViewGroup,
        unitId: String?,
        context: Context, adCallBack: AdCallBack
    ) {
        adContainer.removeAllViews()
        if (context.isNetworkAvailable() && isEnable) {
            adContainer.addView(
                LayoutInflater.from(context).inflate(R.layout.loading_banner_layout, null, false)
            )
            val adView = AdView(context)
            adView.adUnitId = unitId ?: "ca-app-pub-3940256099942544/6300978111"
            adView.setAdSize(getAdSize(context))
            adView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    adContainer.removeAllViews()
                    adCallBack.onAdLoaded()
                    adContainer.addView(adView)
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    OpenAdConfig.isOpenAdStop = true

                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    adCallBack.onAdFailToLoad(Exception(p0.message))
                    adContainer.removeAllViews()
                }
            }
            adView.loadAd(AdRequest.Builder().build())
        }
    }

    private fun getAdSize(context: Context): AdSize {
        val outMetrics = context.resources.displayMetrics
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
            context,
            adWidth
        )
    }


}
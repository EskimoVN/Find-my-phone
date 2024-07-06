package com.tunv.admob.common.nativeAds

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.tunv.admob.R
import com.tunv.admob.common.callback.AdCallBack
import com.tunv.admob.common.utils.FirebaseAnalyticsUtil
import com.tunv.admob.common.utils.isNetworkAvailable

object NativeAdsUtil {

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.apply {
            if (findViewById<View>(R.id.ad_media) != null) mediaView = findViewById(R.id.ad_media)
            headlineView = findViewById(R.id.ad_headline)
            bodyView = findViewById(R.id.ad_body)
               try {
                starRatingView = findViewById(R.id.ad_stars)
                if (findViewById<View>(R.id.ad_advertiser) != null)
                    advertiserView = findViewById(R.id.ad_advertiser)
                } catch (ex: Exception) {

            }
            callToActionView = findViewById(R.id.ad_call_to_action)
            iconView = findViewById(R.id.ad_app_icon)
            (headlineView as TextView).text = nativeAd.headline
            nativeAd.mediaContent?.let {
                mediaView?.mediaContent = it
            }
        }
        nativeAd.starRating?.let { ratings ->
            adView.starRatingView?.visibility = View.VISIBLE
            if (adView.starRatingView != null) {
                (adView.starRatingView as RatingBar).rating = ratings.toFloat()
            } else {
                adView.starRatingView?.visibility = View.GONE
            }
        } ?: run {
            adView.starRatingView?.visibility = View.GONE
        }
        nativeAd.body?.let { body ->
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = body
        } ?: run {
            adView.bodyView?.visibility = View.INVISIBLE
        }

        nativeAd.advertiser?.let { advertiser ->
            adView.advertiserView?.visibility = View.VISIBLE
            if (adView.advertiserView != null) {
                (adView.advertiserView as TextView).text = advertiser
            }
        } ?: run {
            adView.advertiserView?.visibility = View.GONE
        }

        nativeAd.callToAction?.let { callToAction ->
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = callToAction
        } ?: run {
            adView.callToActionView?.visibility = View.INVISIBLE
        }

        nativeAd.icon?.let {
            adView.iconView?.visibility = View.VISIBLE
            adView.iconView?.let { iconView ->
                (iconView as ImageView).setImageDrawable(it.drawable)
            }
        } ?: run {
            adView.iconView?.visibility = View.INVISIBLE
        }

        adView.setNativeAd(nativeAd)
        nativeAd.mediaContent?.let {
            val vc = it.videoController
            vc.apply {
                if (hasVideoContent()) {
                    this.mute(true)
                }
            }
        }

    }


    fun loadNativeAd(
        @LayoutRes loadingAdView: Int,
        adContainerView: ViewGroup,
        nativeId: String,
        context: Activity,
        adListener: AdCallBack? = null
    ) {
        if (context.isNetworkAvailable()) {
            val loadingView = LayoutInflater.from(context).inflate(loadingAdView, null, false)
            adContainerView.removeAllViews()
            adContainerView.addView(loadingView)

            val adLoader = AdLoader.Builder(context, nativeId)
                .forNativeAd { nativeAd ->
                    adListener?.onNativeAdLoad(nativeAd)
                }.withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        adListener?.onAdFailToLoad(Exception(adError.message))
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        adListener?.onAdClick()
                    }
                }).withNativeAdOptions(
                    NativeAdOptions.Builder().setRequestMultipleImages(true)
                        .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                        .setRequestCustomMuteThisAd(true).build()
                ).build()
            adLoader.loadAd(AdRequest.Builder().build())
        } else {
            adContainerView.removeAllViews()
            adListener?.onAdFailToShow(Exception("Internet not connected"))
        }
    }

    fun loadNativeAd(
        nativeId: String, context: Activity, adListener: AdCallBack? = null
    ) {
        if (context.isNetworkAvailable()) {
            val adLoader = AdLoader.Builder(context, nativeId).forNativeAd { nativeAd ->
                adListener?.onNativeAdLoad(nativeAd)
            }.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adListener?.onAdFailToLoad(Exception(adError.message))
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    adListener?.onAdClick()
                }
            }).withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setVideoOptions(VideoOptions.Builder().setStartMuted(true).build())
                    .build()
            ).build()
            adLoader.loadAd(AdRequest.Builder().build())
        } else {
            adListener?.onAdFailToShow(Exception("Internet not connected"))
        }
    }

    fun populateNativeAd(
        context: Activity,
        adContainerView: ViewGroup,
        nativeAd: NativeAd,
        @LayoutRes nativeAdView: Int,
        adListener: AdCallBack? = null
    ) {
        nativeAd.setOnPaidEventListener { paid ->
            FirebaseAnalyticsUtil.logPaidAdImpression(context, paid)
        }
        adContainerView.removeAllViews()
        val nativeView = LayoutInflater.from(context).inflate(nativeAdView, null, false)
        if (nativeView is NativeAdView) {
            adListener?.onAdShown()
            adContainerView.addView(nativeView)
            populateNativeAdView(nativeAd, nativeView)
            // set on ad click native ad
            nativeView.setOnClickListener {
                adListener?.onAdClick()
            }
        } else {
            adListener?.onAdFailToShow(Exception("Type not match"))
        }

    }
}









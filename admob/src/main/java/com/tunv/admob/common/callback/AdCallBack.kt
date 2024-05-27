package com.tunv.admob.common.callback

import android.view.View
import com.google.android.gms.ads.nativead.NativeAd

abstract class AdCallBack {
    open fun onAdLoaded() {}
    open fun onAdFailToLoad(error: Exception) {}
    open fun onAdShown() {}
    open fun onAdClick() {}
    open fun onAdDismiss() {}
    open fun onAdFailToShow(error: Exception) {}
    open fun onNativeAdLoad(nativeAd: NativeAd, adView: View) {}
    open fun onNativeAdLoad(nativeAd: NativeAd) {}

}
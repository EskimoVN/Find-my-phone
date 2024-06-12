package com.eskimo.findmyphone.locatemydevice.trackmymobile.common

import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.ExtendedLiveData
import com.google.android.gms.ads.nativead.NativeAd

class StorageCommon {
    var nativeAdLanguage = ExtendedLiveData<NativeAd?>()
    var nativeAdLanguage2 = ExtendedLiveData<NativeAd?>()
    var nativeAdOnboarding1 = ExtendedLiveData<NativeAd?>()
    var nativeAdOnboarding2 = ExtendedLiveData<NativeAd?>()
    var nativeAdOnboarding3 = ExtendedLiveData<NativeAd?>()
    var nativeAdHome = ExtendedLiveData<NativeAd?>()
    var nativeAdSetting = ExtendedLiveData<NativeAd?>()
    var nativeAdExitDialog = ExtendedLiveData<NativeAd?>()
}
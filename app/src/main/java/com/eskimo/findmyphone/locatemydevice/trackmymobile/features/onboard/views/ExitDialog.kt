package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.onboard.views


import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.MyApplication
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.setOnSafeClickListener
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.DialogExitBinding
import com.tunv.admob.common.callback.AdCallBack
import com.tunv.admob.common.nativeAds.NativeAdsUtil
import kotlin.system.exitProcess


class ExitDialog(
    val activity: Activity,
) : Dialog(activity) {
    private lateinit var binding: DialogExitBinding
    private var cancelOnTouchOutside = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogExitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
        this.setCanceledOnTouchOutside(cancelOnTouchOutside)
        binding.layoutContainer.setOnSafeClickListener {
            if (cancelOnTouchOutside) dismiss()
        }

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        if (MyApplication.getApplication().nativeExitDialog) {
            showPreNative(activity)
        }

        binding.buttonNegative.setOnSafeClickListener {
            dismiss()
        }
        binding.buttonPositive.setOnSafeClickListener {
            dismiss()
            exitProcess(0)
        }
    }

    private fun showPreNative(activity: Activity) {
        MyApplication.getApplication().getStorageCommon().nativeAdExitDialog.observe(
            activity as AppCompatActivity
        )
        { nativeAd ->
            if (nativeAd != null) {
                NativeAdsUtil.populateNativeAd(
                    activity,
                    binding.frAds,
                    nativeAd,
                    com.tunv.admob.R.layout.custom_native_admod_medium,
                    adListener = object : AdCallBack() {
                    })
            }
        }

    }

}

package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.onboard.views


import android.app.Activity
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.MyApplication
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.setOnSafeClickListener
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.DialogExitBinding
import com.eskimo.findmyphone.locatemydevice.trackmymobile.sdptracking.DSPDetectService
import com.eskimo.findmyphone.locatemydevice.trackmymobile.sdptracking.ServiceManager
import com.eskimo.findmyphone.locatemydevice.trackmymobile.servicestracking.AudioDetectService
import com.tunv.admob.common.callback.AdCallBack
import com.tunv.admob.common.nativeAds.NativeAdsUtil


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
            val intent = Intent(activity, DSPDetectService::class.java)
            val notificationManager =
                activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(333)
            ServiceManager.dspDetectService?.resetDataService()
            activity.moveTaskToBack(true)
            activity.finishAffinity()
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

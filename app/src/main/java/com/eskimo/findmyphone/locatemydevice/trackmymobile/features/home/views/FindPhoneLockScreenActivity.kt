package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.home.views

import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.core.os.bundleOf
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.setOnSafeClickListener
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.ui.BaseActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.ActivityFindPhoneLockScreenBinding
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.onboard.views.SplashActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.servicestracking.AudioDetectService

class FindPhoneLockScreenActivity : BaseActivity() {
    private lateinit var binding: ActivityFindPhoneLockScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindPhoneLockScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(20000)
        showOnLockscreen()
        initViews()
    }

    private fun showOnLockscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)
        }
    }

    private fun initViews() {
        binding.root.setOnSafeClickListener {
            endServiceDetect()
            startMainActivityAndFinish()
            startServiceDetect()
        }
    }

    private fun endServiceDetect() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(333)
        val serviceIntent = Intent(this, AudioDetectService::class.java)
        stopService(serviceIntent)
    }

    private fun startServiceDetect() {
        val serviceIntent = Intent(this, AudioDetectService::class.java)
        startService(serviceIntent)
    }


    private fun startMainActivityAndFinish() {
//        val intent = Intent(this, SplashActivity::class.java)
//        // Set flags to clear the activity if active and start a new one
//        // Feel free to change the flags to suit your needs
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//        // Put any arguments here
//        intent.putExtras(bundleOf())
        finish()
//        dismissKeyguard()
//        startActivity(intent)
    }

    private fun dismissKeyguard() {
        with(getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestDismissKeyguard(this@FindPhoneLockScreenActivity, null)
            }
        }
    }
}
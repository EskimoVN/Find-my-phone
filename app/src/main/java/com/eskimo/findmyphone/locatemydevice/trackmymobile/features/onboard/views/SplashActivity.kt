package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.onboard.views

import android.content.Intent
import android.os.Bundle
import com.eskimo.findmyphone.locatemydevice.trackmymobile.R
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.SharedPreferencesManager
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.ui.BaseActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.home.views.HomeActivity

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (!SharedPreferencesManager.getFirstOpen()) {
            startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
        } else {
            startActivity(Intent(this@SplashActivity, OnBoardActivity::class.java))
        }

    }
}
package com.eskimo.findmyphone.locatemydevice.trackmymobile.common.ui

import androidx.appcompat.app.AppCompatActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.SharedPreferencesManager

open class BaseActivity : AppCompatActivity() {
    private var selectedLanguageIndex = SharedPreferencesManager.getAppLanguage()
    override fun onResume() {
        super.onResume()
        if (selectedLanguageIndex != SharedPreferencesManager.getAppLanguage()) {
            recreate()
        }
    }
}
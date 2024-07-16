package com.eskimo.findmyphone.locatemydevice.trackmymobile.common.ui

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.SharedPreferencesManager
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.language.datas.LocaleUtils
import java.util.Locale

open class BaseActivity : AppCompatActivity() {
    private var selectedLanguageIndex = SharedPreferencesManager.getAppLanguage()
    override fun onResume() {
        super.onResume()
        if (selectedLanguageIndex != SharedPreferencesManager.getAppLanguage()) {
            recreate()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val localeToSwitchTo = SharedPreferencesManager.getAppLanguage()
        val localeUpdatedContext: ContextWrapper =
            LocaleUtils.updateLocale(newBase, Locale(localeToSwitchTo))
        super.attachBaseContext(localeUpdatedContext)
    }
}
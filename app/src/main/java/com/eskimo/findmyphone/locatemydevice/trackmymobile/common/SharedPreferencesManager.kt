package com.eskimo.findmyphone.locatemydevice.trackmymobile.common

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {
    private const val SHARED_PREFERENCES_NAME = "SHARED_PREFERENCES_NAME"
    private const val KEY_IS_FIRST_OPEN = "IS_FIRST_OPEN"
    private const val KEY_LANGUAGE_APP = "LANGUAGE_APP"
    private const val KEY_AUTO_FOCUS = "AUTO_FOCUS"
    private const val KEY_VIBRATE = "KEY_VIBRATE"

    private fun getInstance(): SharedPreferences {
        return MyApplication.getApplication().getSharedPreferences(
            SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE
        )
    }

    fun setFirstOpen(firstOpen: Boolean) {
        getInstance().edit().putBoolean(KEY_IS_FIRST_OPEN, firstOpen).apply()
    }

    fun getFirstOpen(): Boolean {
        return getInstance().getBoolean(KEY_IS_FIRST_OPEN, true)
    }

    fun setAppLanguage(value: String) {
        getInstance().edit().putString(KEY_LANGUAGE_APP, value).apply()
    }

    fun getAppLanguage(): String {
        return getInstance().getString(KEY_LANGUAGE_APP, "en") ?: "en"
    }

    fun setAutoFocus(autoFocus: Boolean) {
        getInstance().edit().putBoolean(KEY_AUTO_FOCUS, autoFocus).apply()
    }

    fun getAutoFocus(): Boolean {
        return getInstance().getBoolean(KEY_AUTO_FOCUS, true)
    }

    fun setVibrate(vibrate: Boolean) {
        getInstance().edit().putBoolean(KEY_VIBRATE, vibrate).apply()
    }

    fun getVibrate(): Boolean {
        return getInstance().getBoolean(KEY_VIBRATE, true)
    }

}

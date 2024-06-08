package com.eskimo.findmyphone.locatemydevice.trackmymobile.common

import android.content.Context
import android.content.SharedPreferences
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.models.TypeFlash

object SharedPreferencesManager {
    private const val SHARED_PREFERENCES_NAME = "SHARED_PREFERENCES_NAME"
    private const val KEY_IS_FIRST_OPEN = "IS_FIRST_OPEN"
    private const val KEY_LANGUAGE_APP = "LANGUAGE_APP"
    private const val KEY_AUTO_FOCUS = "AUTO_FOCUS"
    private const val KEY_VIBRATE = "KEY_VIBRATE"
    private const val KEY_STATE_POWER = "KEY_STATE_POWER"
    private const val KEY_VALUE_VOLUME = "KEY_VALUE_VOLUME"
    private const val KEY_VALUE_FLASH = "KEY_VALUE_FLASH"
    private const val KEY_ID_RING_TONE = "KEY_ID_RING_TONE"
    const val KEY_OPEN_SETTING = "KEY_OPEN_SETTING"


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

    fun getStatePower(): Boolean? {
        return getInstance().getBoolean(KEY_STATE_POWER, false)
    }

    fun setStatePower(statePower: Boolean) {
        getInstance().edit().putBoolean(KEY_STATE_POWER, statePower).apply()
    }

    fun setValueVolume(volume: Int) {
        getInstance().edit().putInt(KEY_VALUE_VOLUME, volume).apply()
    }

    fun getValueVolume(): Int {
        return getInstance().getInt(KEY_VALUE_VOLUME, 50)
    }

    fun setValueFlash(flash: Int) {
        getInstance().edit().putInt(KEY_VALUE_FLASH, flash).apply()
    }

    fun getValueFlash(): Int {
        return getInstance().getInt(KEY_VALUE_FLASH, TypeFlash.MEDIUM.time)
    }

    fun setIdRingTone(id: Int) {
        getInstance().edit().putInt(KEY_ID_RING_TONE, id).apply()
    }

    fun getIdRingTone(): Int {
        return getInstance().getInt(KEY_ID_RING_TONE, 0)
    }
}

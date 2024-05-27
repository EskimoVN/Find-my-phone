package com.eskimo.findmyphone.locatemydevice.trackmymobile.common

import android.annotation.SuppressLint
import android.app.Application
import com.google.firebase.FirebaseApp
import com.tunv.admob.common.AdSDK
import com.tunv.admob.common.openAd.OpenAdManager

class MyApplication : Application() {
    private lateinit var storageCommon: StorageCommon

    companion object {
        private lateinit var context: MyApplication
        fun getApplication(): MyApplication {
            return context
        }

    }

    fun getStorageCommon(): StorageCommon {
        return storageCommon
    }

    @SuppressLint("NewApi")
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        context = this
        storageCommon = StorageCommon()
        AdSDK.initialize(this@MyApplication, listOf(""), true)
        OpenAdManager(
            this,
            "ca-app-pub-3940256099942544/9257395921",
        )
    }

}
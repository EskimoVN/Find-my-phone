package com.eskimo.findmyphone.locatemydevice.trackmymobile.common

import android.annotation.SuppressLint
import android.app.Application
import com.google.firebase.FirebaseApp

class MyApplication : Application() {
    companion object {
        private lateinit var context: MyApplication
        fun getApplication(): MyApplication {
            return context
        }

    }

    @SuppressLint("NewApi")
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        context = this
    }

}
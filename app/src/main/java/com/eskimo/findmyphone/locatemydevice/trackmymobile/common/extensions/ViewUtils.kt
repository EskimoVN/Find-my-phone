package com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.annotation.NonNull
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

object ViewUtils {
    private const val SAFE_CLICK_DURATION_MS = 500L

    @SuppressLint("CheckResult")
    fun setOnSafeClickListener(view: View, onClickListener: View.OnClickListener) {
        RxView.clicks(view).throttleFirst(SAFE_CLICK_DURATION_MS, TimeUnit.MILLISECONDS)
            .subscribe({
                onClickListener.onClick(view)
            }, {})
    }

    fun startActivityWithTransition(@NonNull activity: Activity, @NonNull intent: Intent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            activity.startActivity(
                intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
            )
        } else {
            activity.startActivity(intent)
        }
    }
}

fun TextView.getTextIfNotEmpty(): String? {
    return if (text.isNullOrEmpty()) {
        null
    } else {
        text.toString()
    }
}
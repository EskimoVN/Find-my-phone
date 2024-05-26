package com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object Extensions {
    fun generatePermissionReadStorage(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        }
    }

    fun Activity.shareText(valueText: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain";
        intent.putExtra(Intent.EXTRA_TEXT, valueText);
        this.startActivity(Intent.createChooser(intent, "Share QR Text"))
    }

    fun Activity.shareImage(valueText: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/jpeg"
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(valueText))
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        this.startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }

    fun View.gone() {
        visibility = View.GONE
    }

    fun View.visible() {
        visibility = View.VISIBLE
    }

    fun View.invisible() {
        visibility = View.INVISIBLE
    }

    fun TextView.setButtonEnabled(enabled: Boolean) {
        isClickable = enabled
        isFocusable = enabled
        isEnabled = enabled
    }

    fun View.setOnSafeClickListener(onClickListener: View.OnClickListener) {
        ViewUtils.setOnSafeClickListener(this, onClickListener)
    }

    fun checkRequiredFieldsNotEmpty(vararg textViews: TextView): Boolean {
        for (textView in textViews) {
            if (textView.text?.trim().isNullOrEmpty()) {
                return false
            }
        }
        return true
    }

    val Context.wifiManager: WifiManager
        get() = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    fun String.toCaps(): String {
        return toUpperCase(Locale.ROOT)
    }

    fun String.parseStringToCalendar(): Calendar {
        val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale.ENGLISH)
        val calendar = Calendar.getInstance()
        calendar.time = dateFormat.parse(this)!!
        return calendar
    }

    fun Calendar.parseCalendarToString(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss")
        return dateFormat.format(this.time)
    }

    fun String.getLatLngFromGeoUri(): Pair<Double, Double>? {
        val coordinatePattern = Regex("[-+]?[0-9]*\\.?[0-9]+")
        val matches = coordinatePattern.findAll(this).map { it.value.toDouble() }.toList()

        return if (matches.size == 2) {
            Pair(matches[0], matches[1])
        } else {
            null
        }
    }

//    fun Context.logEvent(eventType: String, bundle: Bundle? = null) {
//        val firebaseAnalytics = FirebaseAnalytics.getInstance(this)
//        firebaseAnalytics.logEvent(eventType, bundle)
//    }
}
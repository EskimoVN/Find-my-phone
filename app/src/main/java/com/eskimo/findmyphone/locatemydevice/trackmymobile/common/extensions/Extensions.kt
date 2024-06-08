package com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.TextView
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.models.RingTone

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

    fun Int.getNameAndResourceRingTone(): Pair<String, Int> {
        RingTone.values().forEach {
            if (this == it.id) {
                return Pair(it.nameRingTone, it.resource)
            }
        }
        return Pair(RingTone.POLICE_SIREN.nameRingTone, RingTone.POLICE_SIREN.resource)
    }
}
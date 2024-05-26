package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.language.datas

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.LocaleList
import com.eskimo.findmyphone.locatemydevice.trackmymobile.R
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.MyApplication
import java.util.Locale

class LocaleUtils(base: Context) : ContextWrapper(base) {

    companion object {
        fun updateLocale(c: Context, localeToSwitchTo: Locale): ContextWrapper {
            var context = c
            val resources: Resources = context.resources
            val configuration: Configuration = resources.configuration
            val localeList = LocaleList(localeToSwitchTo)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
            context = context.createConfigurationContext(configuration)
            return LocaleUtils(context)
        }

        val listLanguage = listOf(
            Language(
                "en",
                MyApplication.getApplication().getString(R.string.english),
                R.drawable.ic_language_en,
            ),
            Language(
                "hi",
                MyApplication.getApplication().getString(R.string.hi),
                R.drawable.ic_language_hi,
            ),
            Language(
                "pt",
                MyApplication.getApplication().getString(R.string.portugal),
                R.drawable.ic_language_pt,
            ),
            Language(
                "fr",
                MyApplication.getApplication().getString(R.string.fr),
                R.drawable.ic_language_fr,
            ),
            Language(
                "es",
                MyApplication.getApplication().getString(R.string.es),
                R.drawable.ic_language_es,
            ),
        )
    }
}

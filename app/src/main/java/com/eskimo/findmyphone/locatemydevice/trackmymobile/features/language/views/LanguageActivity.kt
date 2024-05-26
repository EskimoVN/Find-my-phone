package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.language.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.SharedPreferencesManager
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.setOnSafeClickListener
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.ActivityLanguageBinding
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.home.views.HomeActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.language.datas.Language
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.language.datas.LocaleUtils

class LanguageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLanguageBinding
    private lateinit var adapter: LanguageAdapter
    private lateinit var languages: List<Language>
    private var languageSelected: String = SharedPreferencesManager.getAppLanguage()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAdapter()
        setupViews()
    }

    private fun setupViews() {
        binding.buttonNext.setOnSafeClickListener{
            startActivity(Intent(this,HomeActivity::class.java))
        }
    }

    private fun setupAdapter() {
        languages = LocaleUtils.listLanguage
        adapter = LanguageAdapter(this,languages) {
            languageSelected = it.code
        }
        binding.recyclerView.adapter = adapter
    }
}
package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.setOnSafeClickListener
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.ui.BaseActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.ActivitySelectRingToneBinding
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.models.RingToneModel
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.viewmodels.SelectRingtoneViewModel

class SelectRingToneActivity : BaseActivity() {
    private lateinit var binding: ActivitySelectRingToneBinding
    private lateinit var adapter: RingToneAdapter
    private val viewModel: SelectRingtoneViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectRingToneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        val adapter = RingToneAdapter(
            viewModel.idRingTone.value!!,
            viewModel.listItem,
            object : RingToneAdapter.Callback {
                override fun onSelectRingTone(itemSelected: RingToneModel) {
                    viewModel.updateId(itemSelected.id)
                }
            },
            this
        )
        binding.recyclerView.adapter = adapter
        binding.buttonBack.setOnSafeClickListener{
            val returnIntent = Intent()
            returnIntent.putExtra("value", viewModel.idRingTone.value)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }
}
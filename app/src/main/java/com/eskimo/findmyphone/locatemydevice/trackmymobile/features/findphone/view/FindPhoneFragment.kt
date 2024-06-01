package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.view

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eskimo.findmyphone.locatemydevice.trackmymobile.R
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.SharedPreferencesManager
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.gone
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.setOnSafeClickListener
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.visible
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.ui.BaseLazyInflatingFragment
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.FragmentFindPhoneBinding
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.models.FlashModel
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.viewmodels.FindPhoneViewModel
import com.eskimo.findmyphone.locatemydevice.trackmymobile.servicestracking.AudioDetectService

class FindPhoneFragment : BaseLazyInflatingFragment() {
    private lateinit var binding: FragmentFindPhoneBinding
    private lateinit var viewModel: FindPhoneViewModel
    override fun onCreateViewAfterViewStubInflated(
        inflatedView: View?,
        savedInstanceState: Bundle?,
    ) {
        binding = FragmentFindPhoneBinding.bind(inflatedView!!)
        viewModel = FindPhoneViewModelFactory().create(FindPhoneViewModel::class.java)
        observerUI()
        setupViews()

    }

    private fun observerUI() {
        viewModel.statePower.observe(this) {
            val newValue = it ?: false
            binding.iconPower.isSelected = newValue
            SharedPreferencesManager.setStatePower(newValue)
            if (newValue) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissionRecord.launch(android.Manifest.permission.RECORD_AUDIO)
                } else {
                    startServiceDetect()
                }
                binding.textViewStatus.text = getString(R.string.text_activated)
                binding.textViewDescription.text = getString(R.string.text_content_actived)
            } else {
                endServiceDetect()
                binding.textViewStatus.text = getString(R.string.text_deactivate)
                binding.textViewDescription.text = getString(R.string.text_content_deactivate)
            }
        }

        //State modify
        viewModel.stateModify.observe(this) {
            if (it) {
                binding.layoutModify.visible()
                binding.layoutNoModify.gone()
            } else {
                binding.layoutModify.gone()
                binding.layoutNoModify.visible()
            }
        }

        //Value Volume
        viewModel.valueVolume.observe(this) {
            binding.seekBarValueVolume.progress = it
            if (it > 0) binding.textViewRingtone.text = getString(R.string.text_ringtone_on)
            else binding.textViewRingtone.text = getString(R.string.text_ringtone_off)
        }

        //Value Flash
        viewModel.typeFlash.observe(this) {
            binding.textViewValueFlashRate.text = it.name
        }

        //Vibration
        viewModel.typeVibration.observe(this){
            binding.switchValueVibration.isChecked = it!!
        }
    }

    private val requestPermissionRecord =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("LucTV", "Access record success ")
                startServiceDetect()
            } else {
                Log.d("LucTV", "Access record fail ")
            }
        }


    private fun endServiceDetect() {
        val serviceIntent = Intent(requireContext(), AudioDetectService::class.java)
        requireContext().stopService(serviceIntent)
    }

    private fun startServiceDetect() {
        val serviceIntent = Intent(requireContext(), AudioDetectService::class.java)
        requireContext().startService(serviceIntent)
    }

    private fun setupViews() {
        binding.iconPower.setOnSafeClickListener {
            viewModel.setStatePower()
        }
        binding.textViewModify.setOnSafeClickListener {
            viewModel.setStateModify()
        }
        binding.seekBarValueVolume.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                viewModel.updateValueVolume(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        binding.textViewValueFlashRate.setOnSafeClickListener {
            TypeOfFlashBottomSheet(
                viewModel.typeFlash.value!!,
                viewModel.listItem,
                object : TypeOfFlashBottomSheet.Callback {
                    override fun onSelectedTypeGenQR(itemSelected: FlashModel) {
                        viewModel.updateFlashValue(itemSelected)
                    }
                }).show(parentFragmentManager, "")
        }

        binding.switchValueVibration.setOnSafeClickListener {
            viewModel.setVibration()
        }
    }

    override fun getLayoutResource() = R.layout.fragment_find_phone
}

class FindPhoneViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FindPhoneViewModel() as T
    }
}
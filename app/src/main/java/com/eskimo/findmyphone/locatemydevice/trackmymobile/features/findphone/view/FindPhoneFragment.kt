package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.view

import android.app.Activity
import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
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
import com.eskimo.findmyphone.locatemydevice.trackmymobile.BuildConfig
import com.eskimo.findmyphone.locatemydevice.trackmymobile.R
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.MyApplication
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.SharedPreferencesManager
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.getNameAndResourceRingTone
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.gone
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.setOnSafeClickListener
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.visible
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.ui.BaseLazyInflatingFragment
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.FragmentFindPhoneBinding
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.models.FlashModel
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.viewmodels.FindPhoneViewModel
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.setting.views.SettingActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.servicestracking.AudioDetectService
import com.google.android.gms.ads.nativead.NativeAd
import com.tunv.admob.common.callback.AdCallBack
import com.tunv.admob.common.nativeAds.NativeAdsUtil
import com.tunv.admob.common.openAd.OpenAdConfig
import com.tunv.admob.common.utils.isNetworkAvailable

class FindPhoneFragment : BaseLazyInflatingFragment() {
    private lateinit var binding: FragmentFindPhoneBinding
    private lateinit var viewModel: FindPhoneViewModel
    override fun onCreateViewAfterViewStubInflated(
        inflatedView: View?,
        savedInstanceState: Bundle?,
    ) {
        binding = FragmentFindPhoneBinding.bind(inflatedView!!)
        viewModel = FindPhoneViewModelFactory().create(FindPhoneViewModel::class.java)
        endServiceDetect()
        observerUI()
        setupViews()
        setupAds()
    }

    private fun setupAds() {
        OpenAdConfig.enableResumeAd()
        if (requireActivity().isNetworkAvailable() && MyApplication.getApplication().nativeHomeConfig) {
            MyApplication.getApplication().getStorageCommon().nativeAdHome.observe(this)
            {
                if (it != null) {
                    NativeAdsUtil.populateNativeAd(
                        requireActivity(),
                        binding.frAds,
                        it,
                        com.tunv.admob.R.layout.custom_native_admod_medium,
                        adListener = object : AdCallBack() {
                            override fun onAdClick() {
                                super.onAdClick()
                                reloadNativeAd()
                            }
                        })
                }

            }
        } else {
            binding.frAds.gone()
        }
    }

    private fun reloadNativeAd() {
        NativeAdsUtil.loadNativeAd(
            nativeId = BuildConfig.ad_native_language,
            context = requireActivity(),
            adListener = object : AdCallBack() {
                override fun onNativeAdLoad(nativeAd: NativeAd) {
                    super.onNativeAdLoad(nativeAd)
                    MyApplication.getApplication().getStorageCommon().nativeAdHome.setValue(
                        nativeAd
                    )
                }
            })
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
        viewModel.typeVibration.observe(this) {
            binding.switchValueVibration.isChecked = it!!
        }

        viewModel.idRingTone.observe(this) {
            binding.textViewValueRingtone.text = it.getNameAndResourceRingTone().first
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


    private fun startServiceDetect() {
        if (!isServiceRunning(requireContext(), AudioDetectService::class.java)) {
            val serviceIntent = Intent(requireContext(), AudioDetectService::class.java)
            requireContext().startService(serviceIntent)
        }
    }

    private fun endServiceDetect() {
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(333)
        val serviceIntent = Intent(requireContext(), AudioDetectService::class.java)
        requireContext().stopService(serviceIntent)
    }

    private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun setupViews() {
        binding.textViewValueRingtone.setOnSafeClickListener {
            getResultLauncher.launch(Intent(requireContext(), SelectRingToneActivity::class.java))
        }
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

        binding.buttonSetting.setOnSafeClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
        }
    }

    private val getResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val returnValue = data?.getIntExtra("value", 0)!!
            viewModel.updateValueRingtone(returnValue)
        }
    }

    override fun getLayoutResource() = R.layout.fragment_find_phone
}

class FindPhoneViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FindPhoneViewModel() as T
    }
}
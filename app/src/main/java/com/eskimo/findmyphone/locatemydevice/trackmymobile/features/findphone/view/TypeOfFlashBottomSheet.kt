package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eskimo.findmyphone.locatemydevice.trackmymobile.R
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.BottomSheetFlashRateBinding
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.models.FlashModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TypeOfFlashBottomSheet(
    private val itemSelected: FlashModel,
    private val listItem: ArrayList<FlashModel>,
    private val callBack: Callback,
) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetFlashRateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetFlashRateBinding.inflate(layoutInflater)
        fullScreenCall()
        setupsView()
        return binding.root
    }

    private fun fullScreenCall() {
        //for new api versions.
        val decorView = requireActivity().window?.decorView
        val uiOptions =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        if (decorView != null) {
            decorView.systemUiVisibility = uiOptions
        }
    }

    private fun setupsView() {
//        binding.buttonClose.setOnSafeClickListener {
//            dismissAllowingStateLoss()
//        }
        val adapter =
            TypeFlashAdapter(itemSelected, listItem, object : TypeOfFlashBottomSheet.Callback {
                override fun onSelectedTypeGenQR(itemSelected: FlashModel) {
                    callBack.onSelectedTypeGenQR(itemSelected)
                    dismissAllowingStateLoss()
                }

            })
        binding.recyclerViewType.adapter = adapter
    }

    interface Callback {
        fun onSelectedTypeGenQR(itemSelected: FlashModel)
    }
}
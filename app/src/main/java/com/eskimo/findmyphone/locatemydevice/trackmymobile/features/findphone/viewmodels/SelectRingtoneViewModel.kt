package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.SharedPreferencesManager
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.models.RingTone
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.models.RingToneModel

class SelectRingtoneViewModel : ViewModel() {
    private var _idRingTone = MutableLiveData<Int>()
    val idRingTone: LiveData<Int>
        get() = _idRingTone

    val listItem = ArrayList<RingToneModel>()

    init {
        initListRingTone()
    }

    private fun initListRingTone() {
        RingTone.values().forEach {
            val element = RingToneModel(
                name = it.nameRingTone,
                id = it.id,
                image = it.image,
                resource = it.resource
            )
            if (element.id == SharedPreferencesManager.getIdRingTone()) {
                _idRingTone.value = element.id
            }
            listItem.add(
                element
            )
        }
    }

    fun updateId(id: Int) {
        _idRingTone.value = id
    }
}
package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.SharedPreferencesManager
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.viewmodel.BaseViewModel
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.models.FlashModel
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.models.TypeFlash

class FindPhoneViewModel : BaseViewModel() {
    private var _statePower = MutableLiveData<Boolean>()
    val statePower: LiveData<Boolean>
        get() = _statePower

    private var _stateModify = MutableLiveData<Boolean>(true)
    val stateModify: LiveData<Boolean>
        get() = _stateModify

    private var _valueVolume = MutableLiveData<Int>()
    val valueVolume: LiveData<Int>
        get() = _valueVolume

    private var _typeFlash = MutableLiveData<FlashModel>()
    val typeFlash: LiveData<FlashModel>
        get() = _typeFlash

    val listItem = ArrayList<FlashModel>()

    private var _typeVibration = MutableLiveData<Boolean>()
    val typeVibration: LiveData<Boolean>
        get() = _typeVibration

    private var _idRingtone = MutableLiveData<Int>()
    val idRingTone: LiveData<Int>
        get() = _idRingtone

    init {
        getStatePower()
        getVolume()
        getVibration()
        getIdRingTone()
        initListTypeFlash()
    }

    private fun getIdRingTone() {
        _idRingtone.value = SharedPreferencesManager.getIdRingTone()
    }

    private fun initListTypeFlash() {
        TypeFlash.values().forEach {
            val element = FlashModel(
                name = it.nameFlash,
                time = it.time,
                id = it.id,
                isSelected = false
            )
            if (element.time == SharedPreferencesManager.getValueFlash()) {
                _typeFlash.value = element
            }
            listItem.add(
                element
            )
        }
    }

    fun updateFlashValue(flashModel: FlashModel) {
        _typeFlash.value = flashModel
        SharedPreferencesManager.setValueFlash(flashModel.time)
    }

    private fun getVolume() {
        _valueVolume.value = SharedPreferencesManager.getValueVolume()
    }

    fun updateValueVolume(value: Int) {
        _valueVolume.value = value
        SharedPreferencesManager.setValueVolume(value)
    }

    private fun getStatePower() {
        _statePower.value = SharedPreferencesManager.getStatePower()
    }

    fun setStatePower() {
        val newState = _statePower.value ?: false
        _statePower.value = !newState

    }

    fun setStateModify() {
        _stateModify.value = !_stateModify.value!!

    }

    private fun getVibration() {
        _typeVibration.value = SharedPreferencesManager.getVibrate()
    }

    fun setVibration() {
        SharedPreferencesManager.setVibrate(!(_typeVibration.value)!!)
        _typeVibration.value = !_typeVibration.value!!
    }

    fun updateValueRingtone(returnValue: Int) {
        SharedPreferencesManager.setIdRingTone(returnValue)
        _idRingtone.value = returnValue
    }
}
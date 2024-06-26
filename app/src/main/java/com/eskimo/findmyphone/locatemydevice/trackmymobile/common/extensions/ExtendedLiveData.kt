package com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions

import android.util.Pair
import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.*

class ExtendedLiveData<T>() : MutableLiveData<T>() {

    private val observers: MutableList<Pair<LifecycleOwner, Observer<in T>>> = ArrayList()

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        observers.add(Pair(owner, observer))
        super.observe(owner, observer)
    }

    @MainThread
    private fun resetAllObservers() {
        for (observer in observers) {
            super.observe(observer.first, observer.second)
        }
    }

    @MainThread
    private fun removeAllObservers() {
        for (observer in observers) {
            removeObservers(observer.first)
        }
    }

    @MainThread
    fun setValueWithoutNotify(value: T) {
        removeAllObservers()
        super.setValue(value)
    }

    @MainThread
    override fun removeObserver(observer: Observer<in T>) {
        val iterator = observers.iterator()
        while (iterator.hasNext()) {
            val observerItem = iterator.next()
            if (observerItem.second == observer && observerItem.first.lifecycle.currentState == Lifecycle.State.DESTROYED) {
                iterator.remove()
            }
        }
        super.removeObserver(observer)
    }

    override fun setValue(value: T) {
        super.setValue(value)
        if (!hasObservers()) {
            resetAllObservers()
        }
    }

    fun resetValue() {
        super.setValue(null)
    }

}
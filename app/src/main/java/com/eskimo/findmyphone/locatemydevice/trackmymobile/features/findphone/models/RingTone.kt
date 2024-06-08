package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.models

import com.eskimo.findmyphone.locatemydevice.trackmymobile.R
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.MyApplication

enum class RingTone(val id: Int, val nameRingTone: String, val image: Int, val resource: Int) {
    POLICE_SIREN(
        0,
        MyApplication.getApplication().getString(R.string.police_siren),
        R.drawable.image_police,
        R.raw.demo
    ),
    HELLO_MALE(
        1,
        MyApplication.getApplication().getString(R.string.hello_male),
        R.drawable.image_hello_male,
        R.raw.demo
    ),
    HELLO_FEMALE(
        2,
        MyApplication.getApplication().getString(R.string.hello_female),
        R.drawable.image_hello_female,
        R.raw.demo
    ),
    DOG(
        3,
        MyApplication.getApplication().getString(R.string.dog),
        R.drawable.image_dog,
        R.raw.demo
    ),
    CAT(
        4,
        MyApplication.getApplication().getString(R.string.cat),
        R.drawable.image_cat,
        R.raw.demo
    )
}
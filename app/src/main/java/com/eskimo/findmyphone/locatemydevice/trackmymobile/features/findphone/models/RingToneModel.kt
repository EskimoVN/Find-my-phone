package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.models

data class RingToneModel(
    val name: String,
    val id: Int,
    val image: Int,
    val resource: Int,
    var isSelected: Boolean = false,
)


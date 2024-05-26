package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.language.datas
data class Language(
    val code: String,
    val name: String,
    val idIcon: Int,
    var isSelected: Boolean = false,
)
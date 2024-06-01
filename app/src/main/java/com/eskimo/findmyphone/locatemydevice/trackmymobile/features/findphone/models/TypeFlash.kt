package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.models

enum class TypeFlash( val id: Int,  val nameFlash: String,  val time: Int) {
    VERY_QUICK(1, "Very Quick", 100),
    QUICK(2, "Quick", 200),
    MEDIUM(3, "Medium", 300),
    SLOW(4, "Slow", 400),
    VERY_SLOW(5, "Very Slow", 500),
}
package com.sokoldev.budgo.caregiver.models

import com.google.android.gms.maps.model.LatLng

data class Task(
    val image: Int,
    val price: Int,
    val customerName: String,
    val productName: String,
    val distance: String,
    val quantity: Int,
    val latLng: LatLng
)
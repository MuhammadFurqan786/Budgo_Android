package com.sokoldev.budgo.patient.models

data class Booking(
    val productImage: Int? = null,
    val productName: String? = null,
    var productPrice: Int? = null,
    var productType: String? = null,
    var productQuantity: Int? = null,
    var categoryName: String? = null
)

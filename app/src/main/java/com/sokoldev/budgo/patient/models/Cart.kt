package com.sokoldev.budgo.patient.models

data class Cart(
    val productImage: Int? = null,
    val productName: String? = null,
    var productPrice: Int? = null,
    var productQuantity: Int? = null
)

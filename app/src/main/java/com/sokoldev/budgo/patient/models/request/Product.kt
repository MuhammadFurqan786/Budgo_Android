package com.sokoldev.budgo.patient.models.request


import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("quantity")
    val quantity: Int
)
package com.sokoldev.budgo.patient.models.request


import com.google.gson.annotations.SerializedName

data class PlaceOrderRequest(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("card_holder_name")
    val cardHolderName: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("products")
    val products: List<Product>
)
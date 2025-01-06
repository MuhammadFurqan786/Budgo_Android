package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class BookingDetailsResponse(
    @SerializedName("data")
    val `data`: Booking,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)
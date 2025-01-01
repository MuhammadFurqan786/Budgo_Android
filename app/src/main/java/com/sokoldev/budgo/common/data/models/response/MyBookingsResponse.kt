package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class MyBookingsResponse(
    @SerializedName("data")
    val `data`: com.sokoldev.budgo.common.data.models.response.MyBookingsData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)
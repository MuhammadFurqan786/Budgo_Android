package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class DriverEarningResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)
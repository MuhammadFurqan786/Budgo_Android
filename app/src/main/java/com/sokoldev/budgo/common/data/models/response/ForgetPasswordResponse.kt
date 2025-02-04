package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class ForgetPasswordResponse(
    @SerializedName("data")
    val `data`: ForgetData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)
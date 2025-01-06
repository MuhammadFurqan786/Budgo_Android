package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("data")
    val `data`: LoginData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)
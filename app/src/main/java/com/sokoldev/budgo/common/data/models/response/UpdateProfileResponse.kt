package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class UpdateProfileResponse(
    @SerializedName("data")
    val `data`: User,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)
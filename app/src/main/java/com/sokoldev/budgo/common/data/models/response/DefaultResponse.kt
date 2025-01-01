package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class DefaultResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)
package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class ViewAllResponse(
    @SerializedName("data")
    val `data`: ViewAllData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)
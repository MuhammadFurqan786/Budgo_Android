package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class HomeScreenApiResponse(
    @SerializedName("data")
    val `data`: HomeScreenApiData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)
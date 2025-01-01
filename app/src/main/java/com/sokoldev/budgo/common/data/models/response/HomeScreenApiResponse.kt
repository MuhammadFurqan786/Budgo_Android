package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class HomeScreenApiResponse(
    @SerializedName("data")
    val `data`: com.sokoldev.budgo.common.data.models.response.HomeScreenApiData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)
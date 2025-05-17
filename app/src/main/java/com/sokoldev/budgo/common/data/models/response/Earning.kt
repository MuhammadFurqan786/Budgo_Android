package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class Earning(
    @SerializedName("date")
    val date: String,
    @SerializedName("day")
    val day: String,
    @SerializedName("total_earnings")
    val totalEarnings: Int
)
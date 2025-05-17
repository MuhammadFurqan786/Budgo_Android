package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("acceptance_rate")
    val acceptanceRate: String,
    @SerializedName("delivery_pay")
    val deliveryPay: String,
    @SerializedName("earnings")
    val earnings: List<Earning>,
    @SerializedName("today_date")
    val todayDate: String,
    @SerializedName("today_earnings")
    val todayEarnings: String
)
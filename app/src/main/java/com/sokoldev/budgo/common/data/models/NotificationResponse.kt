package com.sokoldev.budgo.common.data.models


import com.google.gson.annotations.SerializedName

data class NotificationResponse(
    @SerializedName("data")
    val notificationData: NotificationData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)
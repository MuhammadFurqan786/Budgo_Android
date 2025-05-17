package com.sokoldev.budgo.common.data.models


import com.google.gson.annotations.SerializedName

data class NotificationItem(
    @SerializedName("from_id")
    val fromId: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("notification")
    val notification: String,
    @SerializedName("notification_time")
    val notificationTime: Int,
    @SerializedName("notification_type")
    val notificationType: String,
    @SerializedName("order_id")
    val orderId: Int,
    @SerializedName("to_id")
    val toId: Int
)
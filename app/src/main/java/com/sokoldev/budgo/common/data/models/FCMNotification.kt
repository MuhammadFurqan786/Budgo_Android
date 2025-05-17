package com.sokoldev.budgo.common.data.models

data class FCMNotification(
    val to: String,
    val notification: FCMNotificationBody,
    val data: Map<String, String>? = null
)

data class FCMNotificationBody(
    val title: String,
    val body: String
)
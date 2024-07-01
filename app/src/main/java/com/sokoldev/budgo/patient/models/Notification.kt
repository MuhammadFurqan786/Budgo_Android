package com.sokoldev.budgo.patient.models

import com.sokoldev.budgo.common.ui.notification.NotificationType

data class Notification(
    val notificationImage: Int? = null,
    val notificationMessage: String? = null,
    val notificationType: NotificationType? = null
)

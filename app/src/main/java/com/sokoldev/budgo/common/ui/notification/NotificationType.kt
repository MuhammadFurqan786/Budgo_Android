package com.sokoldev.budgo.common.ui.notification

enum class NotificationType(val type: String) {
    DRIVER_DETAILS("driver_details"),
    ORDER_DETAILS("order_details"),
    MESSAGE("message"),
    OTHER("other");

    companion object {
        fun fromString(type: String): NotificationType {
            return when (type) {
                DRIVER_DETAILS.type -> DRIVER_DETAILS
                ORDER_DETAILS.type -> ORDER_DETAILS
                MESSAGE.type -> MESSAGE
                else -> OTHER
            }
        }
    }
}

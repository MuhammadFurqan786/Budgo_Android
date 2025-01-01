package com.sokoldev.budgo.common.data.models

data class ChatMessage(
    val message: String,
    val profileImage: Int,
    val isSender: Boolean
)

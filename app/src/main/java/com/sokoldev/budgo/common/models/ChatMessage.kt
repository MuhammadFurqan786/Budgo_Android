package com.sokoldev.budgo.common.models

data class ChatMessage(
    val message: String,
    val profileImage: Int,
    val isSender: Boolean
)

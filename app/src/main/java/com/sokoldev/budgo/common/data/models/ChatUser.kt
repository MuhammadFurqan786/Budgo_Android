package com.sokoldev.budgo.common.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatUser(
    var uid: String? = null,
    var name: String? = null,
    var profileImageUrl: String? = null,
    var chatKey: String? = null,
    var lastMessage: String? = null,
    var timestamp: Long = 0,
    var isRead: Boolean = true
): Parcelable
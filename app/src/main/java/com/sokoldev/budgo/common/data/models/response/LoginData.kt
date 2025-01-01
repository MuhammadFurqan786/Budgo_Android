package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class LoginData(
    @SerializedName("token")
    val token: String,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("user")
    val user: com.sokoldev.budgo.common.data.models.response.User
)
package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class ForgetData(
    @SerializedName("code")
    val code: Int,
    @SerializedName("email")
    val email: String
)
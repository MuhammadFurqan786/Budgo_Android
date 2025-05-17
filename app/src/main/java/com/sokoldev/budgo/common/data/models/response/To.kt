package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class To(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("profile_image")
    val profileImage: String
)
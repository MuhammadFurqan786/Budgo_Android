package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class Brand(
    @SerializedName("brand_image")
    val brandImage: String,
    @SerializedName("brand_name")
    val brandName: String,
    @SerializedName("id")
    val id: Int
)
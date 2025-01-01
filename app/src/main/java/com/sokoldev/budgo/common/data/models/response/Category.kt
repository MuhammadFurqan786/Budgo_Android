package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("category_image")
    val categoryImage: String,
    @SerializedName("category_name")
    val categoryName: String,
    @SerializedName("dispensory")
    val dispensory: String,
    @SerializedName("dispensory_id")
    val dispensoryId: Int,
    @SerializedName("id")
    val id: Int
)
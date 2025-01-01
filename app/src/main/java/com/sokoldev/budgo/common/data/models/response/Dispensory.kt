package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class Dispensory(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("dispensory_name")
    val dispensoryName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("updated_at")
    val updatedAt: String
)
package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("from")
    val from: From,
    @SerializedName("from_id")
    val fromId: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("order_id")
    val orderId: Int,
    @SerializedName("rating")
    val rating: String,
    @SerializedName("review")
    val review: String,
    @SerializedName("to")
    val to: To,
    @SerializedName("to_id")
    val toId: Int,
    @SerializedName("updated_at")
    val updatedAt: String
)
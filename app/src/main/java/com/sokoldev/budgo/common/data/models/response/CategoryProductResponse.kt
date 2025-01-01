package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class CategoryProductResponse(
    @SerializedName("data")
    val `data`: List<com.sokoldev.budgo.common.data.models.response.Product>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)
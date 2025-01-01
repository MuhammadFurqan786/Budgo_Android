package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class HomeScreenApiData(
    @SerializedName("brands")
    val brands: List<com.sokoldev.budgo.common.data.models.response.Brand>,
    @SerializedName("categories")
    val categories: List<com.sokoldev.budgo.common.data.models.response.Category>
)
package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class HomeScreenApiData(
    @SerializedName("brands")
    val brands: List<Brand>,
    @SerializedName("categories")
    val categories: List<Category>
)
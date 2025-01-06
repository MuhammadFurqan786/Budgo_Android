package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class MenuScreenApiData(
    @SerializedName("new_products")
    val newProducts: List<Product>,
    @SerializedName("popular_edibles")
    val popularEdibles: List<Product>,
    @SerializedName("popular_flowers")
    val popularFlowers: List<Product>,
    @SerializedName("top_seller")
    val topSeller: List<Product>
)
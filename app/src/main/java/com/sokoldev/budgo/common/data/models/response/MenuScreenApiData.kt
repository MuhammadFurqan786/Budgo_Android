package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class MenuScreenApiData(
    @SerializedName("new_products")
    val newProducts: List<com.sokoldev.budgo.common.data.models.response.Product>,
    @SerializedName("popular_edibles")
    val popularEdibles: List<com.sokoldev.budgo.common.data.models.response.PopularEdible>,
    @SerializedName("popular_flowers")
    val popularFlowers: List<com.sokoldev.budgo.common.data.models.response.PopularFlower>,
    @SerializedName("top_seller")
    val topSeller: List<com.sokoldev.budgo.common.data.models.response.TopSeller>
)
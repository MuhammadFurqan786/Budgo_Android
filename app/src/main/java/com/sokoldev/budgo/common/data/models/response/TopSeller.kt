package com.sokoldev.budgo.common.data.models.response


import com.google.gson.annotations.SerializedName

data class TopSeller(
    @SerializedName("brand")
    val brand: String,
    @SerializedName("brand_id")
    val brandId: Int,
    @SerializedName("category")
    val category: String,
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("condition")
    val condition: String,
    @SerializedName("discount_percentage")
    val discountPercentage: String,
    @SerializedName("dispensory")
    val dispensory: Dispensory,
    @SerializedName("id")
    val id: Int,
    @SerializedName("price_currency")
    val priceCurrency: String,
    @SerializedName("product_description")
    val productDescription: String,
    @SerializedName("product_image")
    val productImage: String,
    @SerializedName("product_name")
    val productName: String,
    @SerializedName("product_nature")
    val productNature: String,
    @SerializedName("product_price")
    val productPrice: String,
    @SerializedName("product_type")
    val productType: String,
    @SerializedName("temperature")
    val temperature: String
)
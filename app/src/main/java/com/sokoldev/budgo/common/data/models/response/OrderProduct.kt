package com.sokoldev.budgo.common.data.models.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderProduct(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("order_id")
    val orderId: Int,
    @SerializedName("product")
    val product: Product?,
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("quantity")
    val quantity: String
):Parcelable
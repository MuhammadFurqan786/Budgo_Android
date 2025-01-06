package com.sokoldev.budgo.common.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val productId: Int,
    var quantity: Int,
    val productImage: String,
    val productName: String,
    var productPrice: Int
)

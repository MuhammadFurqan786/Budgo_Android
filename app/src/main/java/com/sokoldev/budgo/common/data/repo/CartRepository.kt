package com.sokoldev.budgo.common.data.repo

import com.sokoldev.budgo.common.data.local.CartDao
import com.sokoldev.budgo.common.data.local.CartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CartRepository(private val cartDao: CartDao) {

    suspend fun addToCart(cartItem: CartItem) {
        // Load all items once
        val allItems = cartDao.getAllCartItems()
        val existingItem = allItems.find { it.productId == cartItem.productId }

        if (existingItem != null) {
            existingItem.quantity++
            cartDao.update(existingItem) // Update the existing item
        } else {
            cartDao.insert(cartItem) // Insert new item
        }
    }

    suspend fun updateQuantity(productId: Int, newQuantity: Int) {
        val existingItem = cartDao.getAllCartItems().find { it.productId == productId }

        if (existingItem != null) {
            if (newQuantity <= 0) {
                cartDao.delete(existingItem) // Remove item if quantity is 0 or less
            } else {
                existingItem.quantity = newQuantity // Update quantity
                cartDao.update(existingItem)
            }
        }
    }

    suspend fun removeFromCart(productId: Int) {
        cartDao.deleteByProductId(productId) // Remove item by product ID
    }

    suspend fun getCartItems(): List<CartItem> {
        return cartDao.getAllCartItems() // Get all cart items
    }

    suspend fun clearCart() {
        cartDao.deleteAllCartItems() // Clear all items from the cart
    }


}

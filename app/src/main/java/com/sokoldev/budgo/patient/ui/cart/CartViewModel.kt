package com.sokoldev.budgo.patient.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sokoldev.budgo.common.data.local.CartDao
import com.sokoldev.budgo.common.data.local.CartItem
import com.sokoldev.budgo.common.data.models.response.DefaultResponse
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.data.repo.AppRepository
import com.sokoldev.budgo.common.data.repo.CartRepository
import com.sokoldev.budgo.patient.models.request.PlaceOrderRequest
import kotlinx.coroutines.launch

class CartViewModel(cartDao: CartDao) : ViewModel() {

    private val _listCart: MutableLiveData<List<CartItem>> = MutableLiveData()
    val listCart: LiveData<List<CartItem>>
        get() = _listCart


    private val _placeOrderResponse: MutableLiveData<ApiResponse<DefaultResponse>> =
        MutableLiveData()
    val placeOrderResponse: LiveData<ApiResponse<DefaultResponse>>
        get() = _placeOrderResponse


    private val repository = CartRepository(cartDao)
    private val appRepository = AppRepository()

    // Method to add item to the cart
    fun addToCart(cartItem: CartItem) {
        viewModelScope.launch {
            repository.addToCart(cartItem)
        }
    }

    // Method to update quantity of an item in the cart
    fun updateQuantity(productId: Int, newQuantity: Int) {
        viewModelScope.launch {
            repository.updateQuantity(productId, newQuantity)
            _listCart.value = repository.getCartItems()
        }
    }

    // Method to remove an item from the cart
    fun removeFromCart(productId: Int) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
            _listCart.value = repository.getCartItems()
        }
    }

    // Method to clear all items from the cart
    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    // Method to get all cart items
    fun getCartItems() {
        viewModelScope.launch {
            val items = repository.getCartItems()
            _listCart.value = items
        }
    }

    fun placeOrder(
        token: String,
        placeOrderRequest: PlaceOrderRequest
    ) {
        viewModelScope.launch {
            _placeOrderResponse.value = ApiResponse.Loading
            val response = appRepository.placeOrder(
                token = token,
                placeOrderRequest
            )
            _placeOrderResponse.value = response
        }
    }


}

package com.sokoldev.budgo.patient.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sokoldev.budgo.R
import com.sokoldev.budgo.patient.models.Cart

class CartViewModel : ViewModel() {

    private val _listCart: MutableLiveData<List<Cart>> = MutableLiveData()
    val listCart: LiveData<List<Cart>>
        get() = _listCart


    fun getCartList() {
        val arraylist = ArrayList<Cart>()
        for (i in 1..3) {
            arraylist.add(Cart(R.drawable.image_1, "Product $i", 50, 2))
        }
        _listCart.value = arraylist
    }

}
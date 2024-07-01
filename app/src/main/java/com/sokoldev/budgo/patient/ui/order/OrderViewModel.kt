package com.sokoldev.budgo.patient.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sokoldev.budgo.R
import com.sokoldev.budgo.patient.models.Booking

class OrderViewModel : ViewModel() {

    private val _listBooking: MutableLiveData<List<Booking>> = MutableLiveData()
    val listBooking: LiveData<List<Booking>>
        get() = _listBooking


    fun getBookingList() {
        val arraylist = ArrayList<Booking>()
        for (i in 1..8) {
            arraylist.add(Booking(R.drawable.image_1, "Product $i", 50, "Hybrid", 2, "Category $i"))
        }
        _listBooking.value = arraylist
    }

}
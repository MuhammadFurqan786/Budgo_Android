package com.sokoldev.budgo.patient.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sokoldev.budgo.common.data.models.response.BookingDetailResponse
import com.sokoldev.budgo.common.data.models.response.MyBookingsResponse
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.data.repo.AppRepository
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {

    private val appRepository = AppRepository()

    private val _apiResponse: MutableLiveData<ApiResponse<MyBookingsResponse>> = MutableLiveData()
    val apiResponse: LiveData<ApiResponse<MyBookingsResponse>>
        get() = _apiResponse

    private val _apiResponseSingleBooking: MutableLiveData<ApiResponse<BookingDetailResponse>> =
        MutableLiveData()
    val apiResponseSingleBooking: LiveData<ApiResponse<BookingDetailResponse>>
        get() = _apiResponseSingleBooking


    fun getMyBookings(token: String) {
        viewModelScope.launch {
            _apiResponse.value = ApiResponse.Loading
            val response = appRepository.myBookingsApi(token)
            _apiResponse.value = response
        }
    }

    fun getBookingById(token: String, bookingId: String) {
        viewModelScope.launch {
            _apiResponseSingleBooking.value = ApiResponse.Loading
            val response = appRepository.getBookingById(token, bookingId)
            _apiResponseSingleBooking.value = response
        }
    }




}
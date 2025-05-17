package com.sokoldev.budgo.caregiver.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sokoldev.budgo.common.data.models.response.DefaultResponse
import com.sokoldev.budgo.common.data.models.response.DispensoryResponse
import com.sokoldev.budgo.common.data.models.response.NewJobResponse
import com.sokoldev.budgo.common.data.models.response.PaymentTokenResponse
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.data.repo.AppRepository
import kotlinx.coroutines.launch
import java.io.File

class JobViewModel : ViewModel() {

    private val appRepository = AppRepository()

    private val _jobsResponse: MutableLiveData<ApiResponse<NewJobResponse>> =
        MutableLiveData()
    val jobsResponse: LiveData<ApiResponse<NewJobResponse>>
        get() = _jobsResponse

    private val _dispensoryResponse: MutableLiveData<ApiResponse<DispensoryResponse>> =
        MutableLiveData()
    val dispensoryResponse: LiveData<ApiResponse<DispensoryResponse>>
        get() = _dispensoryResponse


    private val _orderStatusResponse: MutableLiveData<ApiResponse<List<DefaultResponse>>> =
        MutableLiveData()
    val orderStatusResponse: LiveData<ApiResponse<List<DefaultResponse>>>
        get() = _orderStatusResponse


    private val _paymentToken: MutableLiveData<ApiResponse<PaymentTokenResponse>> =
        MutableLiveData()
    val paymentToken: LiveData<ApiResponse<PaymentTokenResponse>>
        get() = _paymentToken

    private val _reviewResponse: MutableLiveData<ApiResponse<DefaultResponse>> =
        MutableLiveData()
    val reviewResponse: LiveData<ApiResponse<DefaultResponse>>
        get() = _reviewResponse


    fun getNewJobs(
        token: String
    ) {
        viewModelScope.launch {
            _jobsResponse.value = ApiResponse.Loading
            val response = appRepository.getNewJobs(
                token = token
            )
            _jobsResponse.value = response
        }

    }

    fun getDispensories(
        token: String,
        latitude: String,
        longitude: String
    ) {
        viewModelScope.launch {
            _dispensoryResponse.value = ApiResponse.Loading
            val response = appRepository.getNearbyDispensories(
                token = token,
                latitude = latitude,
                longitude = longitude
            )
            _dispensoryResponse.value = response
        }

    }



    fun changeOrderStatus(
        token: String,
        orderId: String,
        status: String
    ) {
        viewModelScope.launch {
            _orderStatusResponse.value = ApiResponse.Loading
            val response = appRepository.changeOrderStatus(
                token = token,
                orderId = orderId,
                status = status
            )
            _orderStatusResponse.value = response
        }
    }

    fun changeOrderStatus(
        token: String,
        orderId: String,
        status: String,
        imageFile: File
    ) {
        viewModelScope.launch {
            _orderStatusResponse.value = ApiResponse.Loading
            val response = appRepository.changeOrderStatus(
                token = token,
                orderId = orderId,
                status = status,
                imageFile = imageFile
            )
            _orderStatusResponse.value = response
        }
    }


    fun getOrderToken(
        token: String,
        amount: String
    ) {
        viewModelScope.launch {
            _paymentToken.value = ApiResponse.Loading
            val response = appRepository.getOrderToken(
                token = token,
                amount = amount
            )
            _paymentToken.value = response
        }
    }


    fun addReview(
        token: String,
        otherUserId: String,
        rating: String,
        review: String,
        orderId: String
    ) {
        viewModelScope.launch {
            _reviewResponse.value = ApiResponse.Loading
            val response = appRepository.addReview(
                token = token,
                otherUserId = otherUserId,
                rating = rating,
                review = review,
                orderId = orderId
                )
            _reviewResponse.value = response
        }
    }


}
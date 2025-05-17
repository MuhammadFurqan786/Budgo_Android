package com.sokoldev.budgo.caregiver.ui.earning

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sokoldev.budgo.common.data.models.response.DriverEarningResponse
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.data.repo.AppRepository
import com.sokoldev.budgo.patient.models.request.PlaceOrderRequest
import kotlinx.coroutines.launch

class EarningViewModel : ViewModel() {

    private val _driverEarning: MutableLiveData<ApiResponse<DriverEarningResponse>> =
        MutableLiveData()
    val driverEarning: LiveData<ApiResponse<DriverEarningResponse>>
        get() = _driverEarning

    private val appRepository = AppRepository()

    fun getDriverEarnings(
        token: String
    ) {
        viewModelScope.launch {
            _driverEarning.value = ApiResponse.Loading
            val response = appRepository.getDriverEarnings(
                token = token
            )
            _driverEarning.value = response
        }
    }



}
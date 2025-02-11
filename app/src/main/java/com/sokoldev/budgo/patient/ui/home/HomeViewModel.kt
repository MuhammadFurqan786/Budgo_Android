package com.sokoldev.budgo.patient.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sokoldev.budgo.common.data.models.response.HomeScreenApiResponse
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.data.repo.AppRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val appRepository = AppRepository()

    private val _apiResponse: MutableLiveData<ApiResponse<HomeScreenApiResponse>> =
        MutableLiveData()
    val apiResponse: LiveData<ApiResponse<HomeScreenApiResponse>>
        get() = _apiResponse


    fun homeScreenApi(
        token: String
    ) {
        viewModelScope.launch {
            _apiResponse.value = ApiResponse.Loading
            val response = appRepository.homeScreenApi(
                "Bearer $token"
            )
            _apiResponse.value = response

        }

    }

}
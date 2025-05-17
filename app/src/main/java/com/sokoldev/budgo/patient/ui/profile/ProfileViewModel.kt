package com.sokoldev.budgo.patient.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sokoldev.budgo.common.data.models.response.DefaultResponse
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.data.repo.AppRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val appRepository = AppRepository()

    private val _updateLocation: MutableLiveData<ApiResponse<DefaultResponse>> = MutableLiveData()
    val updateLocationResponse: LiveData<ApiResponse<DefaultResponse>>
        get() = _updateLocation

    fun updateLocation(token: String, location: String, latitude: String, longitude: String) {
        viewModelScope.launch {
            _updateLocation.value = ApiResponse.Loading
            val response = appRepository.updateLocation(token, location, latitude, longitude)
            _updateLocation.value = response
        }
    }

}
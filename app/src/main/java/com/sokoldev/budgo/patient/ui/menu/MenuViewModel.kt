package com.sokoldev.budgo.patient.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sokoldev.budgo.common.data.models.response.MenuScreenApiResponse
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.data.repo.AppRepository
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {

    private val repository = AppRepository()

    private val _apiResponse: MutableLiveData<ApiResponse<MenuScreenApiResponse>> =
        MutableLiveData()
    val apiResponse: LiveData<ApiResponse<MenuScreenApiResponse>>
        get() = _apiResponse


    fun menuScreenApi(
        token: String
    ) {
        viewModelScope.launch {
            _apiResponse.value = ApiResponse.Loading
            val response = repository.menuScreenApi(
                token
            )

            _apiResponse.value = response

        }

    }

}
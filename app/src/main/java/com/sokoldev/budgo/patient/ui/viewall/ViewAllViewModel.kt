package com.sokoldev.budgo.patient.ui.viewall

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sokoldev.budgo.common.data.models.response.CategoryProductResponse
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.data.repo.AppRepository
import kotlinx.coroutines.launch

class ViewAllViewModel : ViewModel() {

    private val appRepository = AppRepository()

    private val _apiResponse: MutableLiveData<ApiResponse<CategoryProductResponse>> =
        MutableLiveData()
    val apiResponse: LiveData<ApiResponse<CategoryProductResponse>>
        get() = _apiResponse


    fun getProductsByCategoryId(
        token: String,
        categoryId: String
    ) {
        viewModelScope.launch {
            _apiResponse.value = ApiResponse.Loading
            val response = appRepository.productByCategoryIdApi(
                "Bearer $token", categoryId
            )
            _apiResponse.value = response
        }
    }


    fun getProductByBrandID(
        token: String,
        brandId: String
    ) {
        viewModelScope.launch {
            _apiResponse.value = ApiResponse.Loading
            val response = appRepository.productByBrandIdApi(
                "Bearer $token", brandId
            )
            _apiResponse.value = response
        }
    }

}
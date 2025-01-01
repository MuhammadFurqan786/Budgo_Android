package com.sokoldev.budgo.common.data.repo

import com.sokoldev.budgo.common.data.models.response.DefaultResponse
import com.sokoldev.budgo.common.data.models.response.HomeScreenApiResponse
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.data.remote.network.RetrofitClientInstance

class HomeRepository {
     private val apiServices = RetrofitClientInstance.api

    suspend fun homeScreenApi(
        token: String
    ): ApiResponse<HomeScreenApiResponse> {
        return try {
            val response = apiServices.homeScreenApi(
                token = token
            )
            if (response.isSuccessful) {
                ApiResponse.Success(response.body()!!)

            } else {
                ApiResponse.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResponse.Error("Error: ${e.message}")
        }
    }
}
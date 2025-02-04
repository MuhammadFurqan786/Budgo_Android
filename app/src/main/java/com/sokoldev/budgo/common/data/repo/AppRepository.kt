package com.sokoldev.budgo.common.data.repo

import android.util.Log
import com.google.gson.JsonSyntaxException
import com.sokoldev.budgo.common.data.models.response.BookingDetailsResponse
import com.sokoldev.budgo.common.data.models.response.CategoryProductResponse
import com.sokoldev.budgo.common.data.models.response.HomeScreenApiResponse
import com.sokoldev.budgo.common.data.models.response.MenuScreenApiResponse
import com.sokoldev.budgo.common.data.models.response.MyBookingsResponse
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.data.remote.network.RetrofitClientInstance

class AppRepository {
    private val apiServices = RetrofitClientInstance.api

    suspend fun homeScreenApi(
        token: String
    ): ApiResponse<HomeScreenApiResponse> {
        return try {
            val response = apiServices.homeScreenApi(
                token =  token
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

    suspend fun menuScreenApi(token: String): ApiResponse<MenuScreenApiResponse> {
        return try {
            val response = apiServices.menuScreenApi(token = token)
            Log.d("API Response", "Raw Response: ${response.body()}")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    ApiResponse.Success(body)
                } else {
                    ApiResponse.Error("Error: Response body is null")
                }
            } else {
                ApiResponse.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: JsonSyntaxException) {
            ApiResponse.Error("Error parsing JSON: ${e.message}")
        } catch (e: Exception) {
            ApiResponse.Error("Error: ${e.message}")
        }
    }


    suspend fun myBookingsApi(
        token: String
    ): ApiResponse<MyBookingsResponse> {
        return try {
            val response = apiServices.myBookingsApi(
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


    suspend fun myBookingsApi(
        token: String,
        bookingId: String
    ): ApiResponse<BookingDetailsResponse> {
        return try {
            val response = apiServices.bookingDetailsByBookingIdApi(
                token = token, bookingId
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


    suspend fun productByCategoryIdApi(
        token: String,
        categoryId: String
    ): ApiResponse<CategoryProductResponse> {
        return try {
            val response = apiServices.productByCategoryIdApi(
                token = token, categoryId
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

    suspend fun productByBrandIdApi(
        token: String,
        brandId: String
    ): ApiResponse<CategoryProductResponse> {
        return try {
            val response = apiServices.productByBrandId(
                token = token, brandId
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
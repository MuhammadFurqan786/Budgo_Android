package com.sokoldev.budgo.common.data.repo

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.gson.JsonSyntaxException
import com.sokoldev.budgo.common.data.models.NotificationItem
import com.sokoldev.budgo.common.data.models.response.BookingDetailResponse
import com.sokoldev.budgo.common.data.models.response.CategoryProductResponse
import com.sokoldev.budgo.common.data.models.response.DefaultResponse
import com.sokoldev.budgo.common.data.models.response.DispensoryResponse
import com.sokoldev.budgo.common.data.models.response.DriverEarningResponse
import com.sokoldev.budgo.common.data.models.response.HomeScreenApiResponse
import com.sokoldev.budgo.common.data.models.response.MenuScreenApiResponse
import com.sokoldev.budgo.common.data.models.response.MyBookingsResponse
import com.sokoldev.budgo.common.data.models.response.NewJobResponse
import com.sokoldev.budgo.common.data.models.response.PaymentTokenResponse
import com.sokoldev.budgo.common.data.pagingsource.NotificationPagingSource
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.data.remote.network.RetrofitClientInstance
import com.sokoldev.budgo.patient.models.request.PlaceOrderRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

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
            val fullToken = "Bearer $token"
            val response = apiServices.menuScreenApi(token = fullToken)
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
            val fullToken = "Bearer $token"
            val response = apiServices.myBookingsApi(
                token = fullToken
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


    suspend fun getBookingById(
        token: String,
        bookingId: String
    ): ApiResponse<BookingDetailResponse> {
        return try {
            val fullToken = "Bearer $token"
            val response = apiServices.bookingDetailsByBookingIdApi(
                token = fullToken, bookingId
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
            val fullToken = "Bearer $token"
            val response = apiServices.productByCategoryIdApi(
                token = fullToken, categoryId
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
            val fullToken = "Bearer $token"
            val response = apiServices.productByBrandId(
                token = fullToken, brandId
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

    // Caregiver Side Api


    suspend fun getNewJobs(
        token: String,
    ): ApiResponse<NewJobResponse> {
        return try {
            val fullToken = "Bearer $token"
            val response = apiServices.getNewJobs(
                token = fullToken
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


    suspend fun getNearbyDispensories(
        token: String,
        latitude: String,
        longitude: String
    ): ApiResponse<DispensoryResponse> {
        return try {
            val fullToken = "Bearer $token"
            val response = apiServices.getNearbyDispensories(
                token = fullToken, latitude = latitude, longitude = longitude
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


    suspend fun changeOrderStatus(
        token: String,
        orderId: String,
        status: String
    ): ApiResponse<List<DefaultResponse>> {
        return try {
            val fullToken = "Bearer $token"
            val response = apiServices.changeOrderStatus(
                token = fullToken,
                orderId = orderId,
                status = status
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


    suspend fun changeOrderStatus(
        token: String,
        orderId: String,
        status: String,
        imageFile: File
    ): ApiResponse<List<DefaultResponse>> {
        return try {
            val fullToken = "Bearer $token"
            val requestBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("order_id", orderId)
                .addFormDataPart("order_status", status)
                .addFormDataPart(
                    "image", // Field name for the back side file
                    imageFile.name, // File name
                    RequestBody.create("image/*".toMediaTypeOrNull(), imageFile) // File content
                )
                .build()


            val response = apiServices.changeOrderStatus(
                token = fullToken,
                requestBody
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


    suspend fun getOrderToken(
        token: String,
        amount: String,
    ): ApiResponse<PaymentTokenResponse> {
        return try {
            val fullToken = "Bearer $token"
            val response = apiServices.getOrderToken(
                token = fullToken,
                amount = amount
            )

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val responseString =
                        responseBody.toString() // Convert to string only if necessary

                    return when {
                        responseString.contains("client_secret") -> {
                            Log.d("API_RESPONSE", "Success: $responseString")
                            ApiResponse.Success(responseBody)
                        }

                        responseString.contains("error") -> {
                            Log.e("API_RESPONSE", "Error: $responseString")
                            ApiResponse.Error("API Error: $responseString")
                        }

                        else -> {
                            Log.e("API_RESPONSE", "Unexpected response: $responseString")
                            ApiResponse.Error("Unexpected response format")
                        }
                    }
                } else {
                    ApiResponse.Error("Empty response body")
                }
            } else {
                ApiResponse.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResponse.Error("Exception: ${e.message}")
        }
    }


    fun getNotifications(token: String): Pager<Int, NotificationItem> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { NotificationPagingSource(apiServices, "Bearer $token") }
        )
    }


    suspend fun placeOrder(
        token: String,
        placeOrderRequest: PlaceOrderRequest
    ): ApiResponse<DefaultResponse> {
        return try {
            val fullToken = "Bearer $token"
            val response = apiServices.placeOrder(
                token = fullToken,
                placeOrderRequest
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

    suspend fun getDriverEarnings(
        token: String,
    ): ApiResponse<DriverEarningResponse> {
        return try {
            val fullToken = "Bearer $token"
            val response = apiServices.getDriverEarnings(
                token = fullToken,
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

    suspend fun addReview(
        token: String,
        otherUserId: String,
        rating: String,
        review: String,
        orderId: String
    ): ApiResponse<DefaultResponse> {
        return try {
            val fullToken = "Bearer $token"
            val response = apiServices.giveReview(
                token = fullToken,
                otherUserId = otherUserId,
                rating = rating,
                review = review,
                orderId = orderId
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


    suspend fun updateLocation(
        token: String,
        location: String,
        latitude: String,
        longitude: String,
    ): ApiResponse<DefaultResponse> {
        return try {
            val fullToken = "Bearer $token"
            val response = apiServices.updateLocation(
                token = fullToken,
                location = location,
                latitude = latitude,
                longitude = longitude
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
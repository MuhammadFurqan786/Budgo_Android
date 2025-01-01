package com.sokoldev.budgo.common.data.repo

import com.sokoldev.budgo.common.data.remote.network.RetrofitClientInstance
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository() {
    private val apiService = RetrofitClientInstance.api

    suspend fun createUser(
        userId: String,
        fullName: String,
        age: String,
        email: String,
        deviceType: String,
        token: String,
        latitude: String,
        longitude: String,
        phone: String
    ): ApiResponse<UserResponse> {
        return try {
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            val filePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
            val userId = RequestBody.create("text/plain".toMediaTypeOrNull(), userId)
            val fullName = RequestBody.create("text/plain".toMediaTypeOrNull(), fullName)
            val email = RequestBody.create("text/plain".toMediaTypeOrNull(), email)
            val age = RequestBody.create("text/plain".toMediaTypeOrNull(), age)
            val deviceType = RequestBody.create("text/plain".toMediaTypeOrNull(), deviceType)
            val token = RequestBody.create("text/plain".toMediaTypeOrNull(), token)
            val latitude = RequestBody.create("text/plain".toMediaTypeOrNull(), latitude)
            val longitude = RequestBody.create("text/plain".toMediaTypeOrNull(), longitude)
            val phone = RequestBody.create("text/plain".toMediaTypeOrNull(), phone)
            val response = apiService.editUserProfile(
                filePart,
                userId,
                fullName,
                age,
                email,
                deviceType,
                latitude,
                longitude,
                phone
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
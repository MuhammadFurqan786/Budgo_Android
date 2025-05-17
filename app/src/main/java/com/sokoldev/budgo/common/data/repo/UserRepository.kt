package com.sokoldev.budgo.common.data.repo

import com.sokoldev.budgo.common.data.models.response.DefaultResponse
import com.sokoldev.budgo.common.data.models.response.ForgetPasswordResponse
import com.sokoldev.budgo.common.data.models.response.LoginResponse
import com.sokoldev.budgo.common.data.models.response.UpdateProfileResponse
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.data.remote.network.RetrofitClientInstance
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UserRepository() {
    private val apiService = RetrofitClientInstance.api

    suspend fun createCaregiverUser(
        fullName: String,
        email1: String,
        phone1: String,
        dob1: String,
        latitude1: String,
        longitude1: String,
        password1: String,
        type1: String,
        dlFrontSide: File,
        dlBackSide: File,
        cgFrontSide: File,
        cgBackSide: File
    ): ApiResponse<DefaultResponse> {
        return try {
            // Validate files
            if (!dlFrontSide.exists() || !dlBackSide.exists() || !cgFrontSide.exists() || !cgBackSide.exists()) {
                return ApiResponse.Error("Error: One or more files do not exist")
            }

            // Create MultipartBody using MultipartBody.Builder
            val requestBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", fullName)
                .addFormDataPart("email", email1)
                .addFormDataPart("phone", phone1)
                .addFormDataPart("dob", dob1)
                .addFormDataPart("latitude", latitude1)
                .addFormDataPart("longitude", longitude1)
                .addFormDataPart("password", password1)
                .addFormDataPart("type", type1)
                .addFormDataPart(
                    "driving_license_front_side", // Field name for the front side of the driving license
                    dlFrontSide.name, // File name
                    RequestBody.create("image/*".toMediaTypeOrNull(), dlFrontSide) // File content
                )
                .addFormDataPart(
                    "driving_license_back_side", // Field name for the back side of the driving license
                    dlBackSide.name, // File name
                    RequestBody.create("image/*".toMediaTypeOrNull(), dlBackSide) // File content
                )
                .addFormDataPart(
                    "caregiver_card_front_side", // Field name for the front side of the caregiver card
                    cgFrontSide.name, // File name
                    RequestBody.create("image/*".toMediaTypeOrNull(), cgFrontSide) // File content
                )
                .addFormDataPart(
                    "caregiver_card_back_side", // Field name for the back side of the caregiver card
                    cgBackSide.name, // File name
                    RequestBody.create("image/*".toMediaTypeOrNull(), cgBackSide) // File content
                )
                .build()

            // Make the API call
            val response = apiService.signupCareGiver(requestBody)

            // Handle the response
            if (response.isSuccessful && response.body() != null) {
                ApiResponse.Success(response.body()!!)
            } else {
                ApiResponse.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResponse.Error("Error: ${e.message}")
        }
    }

    suspend fun updateProfileImage(
        token: String,
        image: File
    ): ApiResponse<UpdateProfileResponse> {
        return try {
            if (!image.exists()) {
                return ApiResponse.Error("Error: file do not exist")
            }
            val fullToken = "Bearer $token"
            // Create MultipartBody using MultipartBody.Builder
            val requestFile = image.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("profile_image", image.name, requestFile)

            // Make the API call
            val response = apiService.updateProfileImage(fullToken, body)

            // Handle the response
            if (response.isSuccessful && response.body() != null) {
                ApiResponse.Success(response.body()!!)
            } else {
                ApiResponse.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResponse.Error("Error: ${e.message}")
        }
    }

    suspend fun createPatientUser(
        fullName: String,
        email1: String,
        phone1: String,
        dob1: String,
        latitude1: String,
        longitude1: String,
        password1: String,
        type1: String,
        cgFrontSide: File,
        cgBackSide: File
    ): ApiResponse<LoginResponse> {
        return try {
            // Validate files
            if (!cgFrontSide.exists() || !cgBackSide.exists()) {
                return ApiResponse.Error("Error: File does not exist")
            }

            // Create MultipartBody using MultipartBody.Builder
            val requestBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", fullName)
                .addFormDataPart("email", email1)
                .addFormDataPart("phone", phone1)
                .addFormDataPart("dob", dob1)
                .addFormDataPart("latitude", latitude1)
                .addFormDataPart("longitude", longitude1)
                .addFormDataPart("password", password1)
                .addFormDataPart("type", type1)
                .addFormDataPart(
                    "patient_card_front_side", // Field name for the front side file
                    cgFrontSide.name, // File name
                    RequestBody.create("image/*".toMediaTypeOrNull(), cgFrontSide) // File content
                )
                .addFormDataPart(
                    "patient_card_back_side", // Field name for the back side file
                    cgBackSide.name, // File name
                    RequestBody.create("image/*".toMediaTypeOrNull(), cgBackSide) // File content
                )
                .build()

            // Make the API call
            val response = apiService.signupPatient(requestBody)

            // Handle the response
            if (response.isSuccessful && response.body() != null) {
                ApiResponse.Success(response.body()!!)
            } else {
                ApiResponse.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResponse.Error("Error: ${e.message}")
        }
    }

    suspend fun loginUser(
        email: String,
        password: String
    ): ApiResponse<LoginResponse> {
        return try {

            val response = apiService.loginUserApi(
                email,
                password,
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


    suspend fun updateProfile(
        token: String,
        name: String,
        phone: String,
        dob: String,
        latitude: String,
        longitude: String

    ): ApiResponse<UpdateProfileResponse> {
        return try {
            val fullToken = "Bearer $token"
            val response = apiService.updateProfile(
                fullToken,
                name,
                phone,
                dob,
                latitude,
                longitude
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


    suspend fun forgotUserPassword(
        email: String
    ): ApiResponse<ForgetPasswordResponse> {
        return try {
            val response = apiService.forgotUserPasswordApi(
                email
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


    suspend fun logoutUser(
        token: String
    ): ApiResponse<DefaultResponse> {
        return try {
            val fullToken = "Bearer $token"
            val response = apiService.logoutUserApi(
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

    suspend fun changeUserPassword(
        token: String,
        oldPassword: String,
        newPassword: String
    ): ApiResponse<DefaultResponse> {
        return try {
            val fullToken = "Bearer $token"
            val response = apiService.changePasswordApi(
                token = fullToken,
                oldPassword = oldPassword,
                newPassword = newPassword
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



    suspend fun resetUserPassword(
        email: String,
        password: String,
    ): ApiResponse<DefaultResponse> {
        return try {
            val response = apiService.resetUserPasswordApi(
                email,
                password
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

    suspend fun changeFcmToken(
        token: String,
        deviceType: String,
        fcmToken: String
    ): ApiResponse<DefaultResponse> {
        return try {
            val fullToken = "Bearer $token"
            val response = apiService.updateFcmTokenApi(
                fullToken, deviceType, fcmToken
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


    suspend fun deleteAccount(
        token: String
    ): ApiResponse<DefaultResponse> {
        return try {
            val fullToken = "Bearer $token"
            val response = apiService.deleteAccountApi(
                fullToken
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


    suspend fun changeOnlineStatus(
        token: String,
        status: Int
    ): ApiResponse<DefaultResponse> {
        val fullToken = "Bearer $token"
        return try {
            val response = apiService.onlineStatusApi(
                fullToken,
                status
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
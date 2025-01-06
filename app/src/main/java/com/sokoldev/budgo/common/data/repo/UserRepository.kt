package com.sokoldev.budgo.common.data.repo

import com.sokoldev.budgo.common.data.models.response.DefaultResponse
import com.sokoldev.budgo.common.data.models.response.ForgetPasswordResponse
import com.sokoldev.budgo.common.data.models.response.LoginResponse
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.data.remote.network.RetrofitClientInstance
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
            val requestDlFrontSideFile =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), dlFrontSide)
            val dlFrontSideFile =
                MultipartBody.Part.createFormData("driving_license_front_side", dlFrontSide.name, requestDlFrontSideFile)

            val requestDlBackSideFile =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), dlBackSide)
            val dlBackSideFile =
                MultipartBody.Part.createFormData("driving_license_back_side", dlBackSide.name, requestDlBackSideFile)

            val requestCGFrontSideFile =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), cgFrontSide)
            val cgFrontSideFile =
                MultipartBody.Part.createFormData("caregiver_card_front_side", cgFrontSide.name, requestCGFrontSideFile)

            val requestCGBackSideFile =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), cgBackSide)
            val cgBackSideFile =
                MultipartBody.Part.createFormData("caregiver_card_back_side", cgBackSide.name, requestCGBackSideFile)


            val name = RequestBody.create("text/plain".toMediaTypeOrNull(), fullName)
            val email = RequestBody.create("text/plain".toMediaTypeOrNull(), email1)
            val phone = RequestBody.create("text/plain".toMediaTypeOrNull(), phone1)
            val dob = RequestBody.create("text/plain".toMediaTypeOrNull(), dob1)
            val latitude = RequestBody.create("text/plain".toMediaTypeOrNull(), latitude1)
            val longitude = RequestBody.create("text/plain".toMediaTypeOrNull(), longitude1)
            val password = RequestBody.create("text/plain".toMediaTypeOrNull(), password1)
            val type = RequestBody.create("text/plain".toMediaTypeOrNull(), type1)

            val response = apiService.signupCareGiver(
                name,
                email,
                phone,
                dob,
                latitude,
                longitude,
                password,
                type,
                dlFrontSideFile,
                dlBackSideFile,
                cgFrontSideFile,
                cgBackSideFile
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
    ): ApiResponse<DefaultResponse> {
        return try {


            val requestPTFrontSideFile =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), cgFrontSide)
            val ptFrontSideFile =
                MultipartBody.Part.createFormData("patient_card_front_side", cgFrontSide.name, requestPTFrontSideFile)

            val requestPTBackSideFile =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), cgBackSide)
            val ptBackSideFile =
                MultipartBody.Part.createFormData("patient_card_back_side", cgBackSide.name, requestPTBackSideFile)


            val name = RequestBody.create("text/plain".toMediaTypeOrNull(), fullName)
            val email = RequestBody.create("text/plain".toMediaTypeOrNull(), email1)
            val phone = RequestBody.create("text/plain".toMediaTypeOrNull(), phone1)
            val dob = RequestBody.create("text/plain".toMediaTypeOrNull(), dob1)
            val latitude = RequestBody.create("text/plain".toMediaTypeOrNull(), latitude1)
            val longitude = RequestBody.create("text/plain".toMediaTypeOrNull(), longitude1)
            val password = RequestBody.create("text/plain".toMediaTypeOrNull(), password1)
            val type = RequestBody.create("text/plain".toMediaTypeOrNull(), type1)

            val response = apiService.signupPatient(
                name,
                email,
                phone,
                dob,
                latitude,
                longitude,
                password,
                type,
                ptFrontSideFile,
                ptBackSideFile
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
            val response = apiService.logoutUserApi(
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

    suspend fun changeUserPassword(
        token: String,
        oldPassword: String,
        newPassword: String
    ): ApiResponse<DefaultResponse> {
        return try {
            val response = apiService.changePasswordApi(
                token = token,
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
            val response = apiService.updateFcmTokenApi(
                token, deviceType, fcmToken
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
            val response = apiService.deleteAccountApi(
                token
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
        return try {
            val response = apiService.onlineStatusApi(
                token,
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
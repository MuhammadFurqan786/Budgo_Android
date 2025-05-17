package com.sokoldev.budgo.common.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sokoldev.budgo.common.data.models.response.DefaultResponse
import com.sokoldev.budgo.common.data.models.response.ForgetPasswordResponse
import com.sokoldev.budgo.common.data.models.response.LoginResponse
import com.sokoldev.budgo.common.data.models.response.UpdateProfileResponse
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.data.repo.UserRepository
import kotlinx.coroutines.launch
import java.io.File

class UserViewModel : ViewModel() {

    private val userRepository = UserRepository()


    private val _apiResponse: MutableLiveData<ApiResponse<DefaultResponse>> = MutableLiveData()
    val apiResponse: LiveData<ApiResponse<DefaultResponse>>
        get() = _apiResponse


    private val _apiResponseLogin: MutableLiveData<ApiResponse<LoginResponse>> = MutableLiveData()
    val apiResponseLogin: LiveData<ApiResponse<LoginResponse>>
        get() = _apiResponseLogin

    private val _apiResponseProfile: MutableLiveData<ApiResponse<UpdateProfileResponse>> = MutableLiveData()
    val apiResponseProfile: LiveData<ApiResponse<UpdateProfileResponse>>
        get() = _apiResponseProfile


    private val _apiResponseForgotPassword: MutableLiveData<ApiResponse<ForgetPasswordResponse>> =
        MutableLiveData()
    val apiResponseForgotPassword: LiveData<ApiResponse<ForgetPasswordResponse>>
        get() = _apiResponseForgotPassword


    fun registerCaregiver(
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

    ) {
        viewModelScope.launch {
            _apiResponse.value = ApiResponse.Loading
            val response = userRepository.createCaregiverUser(
                fullName,
                email1,
                phone1,
                dob1,
                latitude1,
                longitude1,
                password1,
                type1,
                dlFrontSide,
                dlBackSide,
                cgFrontSide,
                cgBackSide
            )
            _apiResponse.value = response
        }

    }

  fun registerPatient(
        fullName: String,
        email1: String,
        phone1: String,
        dob1: String,
        latitude1: String,
        longitude1: String,
        password1: String,
        type1: String,
        ptFrontSide: File,
        ptBackSide: File

    ) {
        viewModelScope.launch {
            _apiResponseLogin.value = ApiResponse.Loading
            val response = userRepository.createPatientUser(
                fullName,
                email1,
                phone1,
                dob1,
                latitude1,
                longitude1,
                password1,
                type1,
                ptFrontSide,
                ptBackSide
            )

            _apiResponseLogin.value = response
        }

    }


    fun loginUser(
        email1: String,
        password1: String,

    ) {
        viewModelScope.launch {
            _apiResponseLogin.value = ApiResponse.Loading
            val response = userRepository.loginUser(
                email1, password1
            )

            _apiResponseLogin.value = response
        }

    }

    fun updateProfile(
        token: String,
        name: String,
        phone: String,
        dob: String,
        latitude: String,
        longitude: String
    ) {
        viewModelScope.launch {
            _apiResponseProfile.value = ApiResponse.Loading
            val response = userRepository.updateProfile(
                token,
                name, phone, dob, latitude, longitude
            )
            _apiResponseProfile.value = response
        }
    }

    fun updateProfileImage(
        token: String,
        image: File
    ) {
        viewModelScope.launch {
            _apiResponseProfile.value = ApiResponse.Loading
            val response = userRepository.updateProfileImage(
                token,
                image
            )
            _apiResponseProfile.value = response
        }

    }


    fun forgotUserPassword(
        email1: String
    ) {
        viewModelScope.launch {
            _apiResponseForgotPassword.value = ApiResponse.Loading
            val response = userRepository.forgotUserPassword(
                email1
            )

            _apiResponseForgotPassword.value = response
        }

    }


    fun resetUserPassword(
        email1: String,
        password1: String
    ) {
        viewModelScope.launch {
            _apiResponse.value = ApiResponse.Loading
            val response = userRepository.resetUserPassword(
                email1, password1
            )

            _apiResponse.value = response
        }
    }


    fun changeUserPassword(
        token: String,
        oldpassword: String,
        newPassword: String,
    ) {
        viewModelScope.launch {
            _apiResponse.value = ApiResponse.Loading
            val response = userRepository.changeUserPassword(
                token, oldpassword, newPassword
            )

            _apiResponse.value = response
        }

    }


    fun logoutUser(
        token: String,
    ) {
        viewModelScope.launch {
            _apiResponse.value = ApiResponse.Loading
            val response = userRepository.logoutUser(
                token
            )

            _apiResponse.value = response
        }

    }


    fun deleteAccount(
        token: String,
    ) {
        viewModelScope.launch {
            _apiResponse.value = ApiResponse.Loading
            val response = userRepository.deleteAccount(
                token
            )

            _apiResponse.value = response
        }
    }


    fun changeOnlineStatus(
        token: String,
        status: Int
    ) {
        viewModelScope.launch {
            _apiResponse.value = ApiResponse.Loading
            val response = userRepository.changeOnlineStatus(
                token, status
            )

            _apiResponse.value = response
        }
    }


    fun changeFcmToken(
        token: String,
        deviceType: String,
        fcmToken: String
    ) {
        viewModelScope.launch {
            _apiResponse.value = ApiResponse.Loading
            val response = userRepository.changeFcmToken(
                token, deviceType, fcmToken
            )

            _apiResponse.value = response
        }
    }

}
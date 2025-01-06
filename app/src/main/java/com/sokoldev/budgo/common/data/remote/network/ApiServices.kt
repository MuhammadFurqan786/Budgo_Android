package com.sokoldev.budgo.common.data.remote.network

import com.sokoldev.budgo.common.data.models.response.BookingDetailsResponse
import com.sokoldev.budgo.common.data.models.response.CategoryProductResponse
import com.sokoldev.budgo.common.data.models.response.DefaultResponse
import com.sokoldev.budgo.common.data.models.response.ForgetPasswordResponse
import com.sokoldev.budgo.common.data.models.response.HomeScreenApiResponse
import com.sokoldev.budgo.common.data.models.response.LoginResponse
import com.sokoldev.budgo.common.data.models.response.MenuScreenApiResponse
import com.sokoldev.budgo.common.data.models.response.MyBookingsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


interface ApiServices {


    /* Auth Api*/

    @Multipart
    @POST("auth/signup")
    suspend fun signupCareGiver(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("password") password: RequestBody,
        @Part("type") type: RequestBody,
        @Part drivingLicenseFrontSide: MultipartBody.Part,
        @Part drivingLicenseBackSide: MultipartBody.Part,
        @Part careGiverCardFrontSide: MultipartBody.Part,
        @Part careGiverCardBackSide: MultipartBody.Part,
    ): Response<DefaultResponse>


    @Multipart
    @POST("auth/signup")
    suspend fun signupPatient(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("password") password: RequestBody,
        @Part("type") type: RequestBody,
        @Part patientCardFrontSide: MultipartBody.Part,
        @Part patientCardBackSide: MultipartBody.Part
    ): Response<DefaultResponse>

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun loginUserApi(
        @Field("login") email: String,
        @Field("password") password: String,
    ): Response<LoginResponse>


    @GET("auth/forget-password/{email}")
    suspend fun forgotUserPasswordApi(
        @Path("email") email: String,
    ): Response<ForgetPasswordResponse>


    @FormUrlEncoded
    @POST("auth/reset-password")
    suspend fun resetUserPasswordApi(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Response<DefaultResponse>


    @FormUrlEncoded
    @POST("auth/logout")
    suspend fun logoutUserApi(
        @Header("Authorization") token: String
    ): Response<DefaultResponse>


    @FormUrlEncoded
    @POST("auth/change-password")
    suspend fun changePasswordApi(
        @Header("Authorization") token: String,
        @Field("old_password") oldPassword: String,
        @Field("new_password") newPassword: String,
    ): Response<DefaultResponse>

    @FormUrlEncoded
    @POST("auth/update-fcm-token")
    suspend fun updateFcmTokenApi(
        @Header("Authorization") token: String,
        @Field("device_type") deviceType: String,
        @Field("token") fcmToken: String,
    ): Response<DefaultResponse>


    @POST("auth/delete-account")
    suspend fun deleteAccountApi(
        @Header("Authorization") token: String
    ): Response<DefaultResponse>


    @POST("auth/change-online-status")
    suspend fun onlineStatusApi(
        @Header("Authorization") token: String,
        @Field("status") status: Int,
    ): Response<DefaultResponse>


    /* App Api*/

    @GET("app/home")
    suspend fun homeScreenApi(
        @Header("Authorization") token: String
    ): Response<HomeScreenApiResponse>


    @GET("app/menu")
    suspend fun menuScreenApi(
        @Header("Authorization") token: String,
    ): Response<MenuScreenApiResponse>

    @GET("app/my-bookings")
    suspend fun myBookingsApi(
        @Header("Authorization") token: String
    ): Response<MyBookingsResponse>


    @GET("app/booking-details/{id}")
    suspend fun bookingDetailsByBookingIdApi(
        @Header("Authorization") token: String,
        @Path("id") bookingId: String
    ): Response<BookingDetailsResponse>


    @GET("app/category-products/{category_id}")
    suspend fun productByCategoryIdApi(
        @Header("Authorization") token: String,
        @Path("category_id") categoryId: String
    ): Response<CategoryProductResponse>




}
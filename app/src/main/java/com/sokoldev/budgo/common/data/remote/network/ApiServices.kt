package com.sokoldev.budgo.common.data.remote.network

import com.sokoldev.budgo.common.data.models.NotificationResponse
import com.sokoldev.budgo.common.data.models.response.BookingDetailResponse
import com.sokoldev.budgo.common.data.models.response.CategoryProductResponse
import com.sokoldev.budgo.common.data.models.response.DefaultResponse
import com.sokoldev.budgo.common.data.models.response.DispensoryResponse
import com.sokoldev.budgo.common.data.models.response.DriverEarningResponse
import com.sokoldev.budgo.common.data.models.response.ForgetPasswordResponse
import com.sokoldev.budgo.common.data.models.response.HomeScreenApiResponse
import com.sokoldev.budgo.common.data.models.response.LoginResponse
import com.sokoldev.budgo.common.data.models.response.MenuScreenApiResponse
import com.sokoldev.budgo.common.data.models.response.MyBookingsResponse
import com.sokoldev.budgo.common.data.models.response.NewJobResponse
import com.sokoldev.budgo.common.data.models.response.PaymentTokenResponse
import com.sokoldev.budgo.common.data.models.response.UpdateProfileResponse
import com.sokoldev.budgo.patient.models.request.PlaceOrderRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiServices {


    /* Auth Api*/

    @POST("auth/signup") // Update the endpoint if necessary
    suspend fun signupCareGiver(
        @Body requestBody: RequestBody
    ): Response<DefaultResponse>

    @POST("auth/signup") // Update the endpoint if necessary
    suspend fun signupPatient(
        @Body requestBody: RequestBody
    ): Response<LoginResponse>




    @FormUrlEncoded
    @POST("auth/login")
    suspend fun loginUserApi(
        @Field("login") email: String,
        @Field("password") password: String,
    ): Response<LoginResponse>


    @GET("auth/forgot-password/{email}")
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
    @POST("auth/update-profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("dob") dob: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String
    ): Response<UpdateProfileResponse>




    @Multipart
    @POST("auth/update-profile")
    suspend fun updateProfileImage(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Response<UpdateProfileResponse>


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


    @GET("auth/delete-account")
    suspend fun deleteAccountApi(
        @Header("Authorization") token: String
    ): Response<DefaultResponse>


    @FormUrlEncoded
    @POST("auth/change-online-status")
    suspend fun onlineStatusApi(
        @Header("Authorization") token: String,
        @Field("status") status: Int,
    ): Response<DefaultResponse>



    /* App Api*/

    @GET("app/home")
    suspend fun homeScreenApi(
        @Header("Authorization") token: String,
        @Header("Content-Type") contentType: String = "application/json"
    ): Response<HomeScreenApiResponse>


    @GET("app/menu")
    suspend fun menuScreenApi(
        @Header("Authorization") token: String,
        @Header("Content-Type") contentType: String = "application/json"
    ): Response<MenuScreenApiResponse>

    @GET("app/my-bookings")
    suspend fun myBookingsApi(
        @Header("Authorization") token: String
    ): Response<MyBookingsResponse>


    @GET("app/booking-details/{id}")
    suspend fun bookingDetailsByBookingIdApi(
        @Header("Authorization") token: String,
        @Path("id") bookingId: String
    ): Response<BookingDetailResponse>


    @GET("app/category-products/{category_id}")
    suspend fun productByCategoryIdApi(
        @Header("Authorization") token: String,
        @Path("category_id") categoryId: String
    ): Response<CategoryProductResponse>


    @GET("app/brand-products/{brand_id}")
    suspend fun productByBrandId(
        @Header("Authorization") token: String,
        @Path("brand_id") brandId: String
    ): Response<CategoryProductResponse>


    @POST("app/new-jobs")
    suspend fun getNewJobs(
        @Header("Authorization") token: String,
        @Header("Content-Type") contentType: String = "application/json"
    ): Response<NewJobResponse>


    @FormUrlEncoded
    @POST("app/change-order-status")
    suspend fun changeOrderStatus(
        @Header("Authorization") token: String,
        @Field("order_id") orderId: String,
        @Field("order_status") status: String,
    ): Response<List<DefaultResponse>>


    @POST("app/change-order-status") // Replace with actual endpoint
    suspend fun changeOrderStatus(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): Response<List<DefaultResponse>>


    @GET("app/notification-list")
    suspend fun getNotifications(
        @Header("Authorization") token: String,
        @Query("page") page: Int
    ): Response<NotificationResponse>

    @FormUrlEncoded
    @POST("app/nearby-dispensories")
    suspend fun getNearbyDispensories(
        @Header("Authorization") token: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String
    ): Response<DispensoryResponse>

    @POST("app/get-order-token")
    suspend fun getOrderToken(
        @Header("Authorization") token: String,
        @Query("amount") amount: String
    ): Response<PaymentTokenResponse>


    @POST("app/place-order")
    suspend fun placeOrder(
        @Header("Authorization") token: String,
        @Body placeOrderRequest: PlaceOrderRequest
    ): Response<DefaultResponse>


    @GET("app/earnings")
    suspend fun getDriverEarnings(
        @Header("Authorization") token: String,
    ): Response<DriverEarningResponse>

    @FormUrlEncoded
    @POST("app/review")
    suspend fun giveReview(
        @Header("Authorization") token: String,
        @Field("to_id") otherUserId: String,
        @Field("rating") rating: String,
        @Field("review") review: String,
        @Field("order_id") orderId: String
    ): Response<DefaultResponse>


    @FormUrlEncoded
    @POST("app/nearby-dispensories")
    suspend fun updateLocation(
        @Header("Authorization") token: String,
        @Field("location") location: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
    ): Response<DefaultResponse>


}
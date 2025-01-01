package com.sokoldev.budgo.common.data.remote.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClientInstance {

    private const val BASE_URL_DEV = "https://codewithfurqan.tech/budgo/public/api/v1/"


    val okHttpBuilder = OkHttpClient.Builder()
        .readTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(5, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_DEV)
        .client(okHttpBuilder)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: ApiServices = retrofit.create(ApiServices::class.java)
}

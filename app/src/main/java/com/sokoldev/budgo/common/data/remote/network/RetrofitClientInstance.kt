package com.sokoldev.budgo.common.data.remote.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClientInstance {

    private const val BASE_URL_DEV = "https://codewithfurqan.tech/budgo/public/api/v1/"

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val okHttpBuilder = OkHttpClient.Builder()
        .readTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(5, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            val rawJson = response.body?.string()
            println("Response: $rawJson")
            response.newBuilder()
                .body(okhttp3.ResponseBody.create(response.body?.contentType(), rawJson ?: ""))
                .build()
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_DEV)
        .client(okHttpBuilder)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val api: ApiServices = retrofit.create(ApiServices::class.java)
}


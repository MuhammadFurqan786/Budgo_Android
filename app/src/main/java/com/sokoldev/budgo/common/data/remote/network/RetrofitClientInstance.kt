package com.sokoldev.budgo.common.data.remote.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RetrofitClientInstance {

    private const val BASE_URL_DEV = "https://budgo.net/budgo/public/api/v1/"

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }


    fun getUnsafeOkHttpClient(): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            }
        )

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        val sslSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(RetrofitClientInstance.logging)
            .build()
    }


    private val okHttpBuilder = OkHttpClient.Builder()
        .readTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(5, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .addInterceptor { chain ->
            val userAgent =
                "BudgoApp/1.0 (Android ${android.os.Build.VERSION.RELEASE}; ${android.os.Build.MODEL}) [Hostinger]"
            val request = chain.request().newBuilder()
                .addHeader("User-Agent", userAgent)
                .build()
            val response = chain.proceed(request)

            val responseBody = response.body
            val rawJson = responseBody?.string() // Read response body as a string

            println("Response: $rawJson") // Log JSON response

            // Create a new response body so Retrofit can read it
            val newResponseBody = rawJson?.toResponseBody(responseBody.contentType())

            response.newBuilder()
                .body(newResponseBody)
                .build()
        }
        .build()


    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_DEV)
        .client(getUnsafeOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val api: ApiServices = retrofit.create(ApiServices::class.java)

}


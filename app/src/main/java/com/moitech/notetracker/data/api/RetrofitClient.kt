package com.moitech.notetracker.data.api

import android.content.Context
import com.moitech.notetracker.data.local.LocalStorage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private var cachedApi: ApiService? = null
    private var currentBaseUrl: String? = null

    fun getService(context: Context): ApiService {
        val baseUrl = LocalStorage.getBaseUrl(context)
        if (cachedApi != null && currentBaseUrl == baseUrl) {
            return cachedApi!!
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        currentBaseUrl = baseUrl
        cachedApi = retrofit.create(ApiService::class.java)
        return cachedApi!!
    }

    fun invalidate() {
        cachedApi = null
    }
}

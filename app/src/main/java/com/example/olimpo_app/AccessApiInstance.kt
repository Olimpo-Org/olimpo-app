package com.example.olimpo_app

import com.example.olimpo_app.data.network.AccessAPIService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AccessApiInstance {
    private const val BASE_URL = "https://olimpoapi.onrender.com"

    val api: AccessAPIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AccessAPIService::class.java)
    }
}
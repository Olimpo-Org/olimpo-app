package com.example.olimpo_app

import com.example.olimpo_app.data.network.FeaturesAPIService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FeaturesApiInstance {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    val api: FeaturesAPIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FeaturesAPIService::class.java)
    }
}
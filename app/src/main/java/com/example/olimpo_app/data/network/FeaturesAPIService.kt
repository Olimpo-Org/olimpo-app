package com.example.olimpo_app.data.network

import com.example.olimpo_app.data.model.feedFlow.Publication
import retrofit2.Response
import retrofit2.http.GET

interface FeaturesAPIService {
    @GET("publication/")
    suspend fun getAllPosts(): Response<List<Publication>>
}
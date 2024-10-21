package com.example.olimpo_app.listeners

import com.example.olimpo_app.data.models.Publication
import retrofit2.Response
import retrofit2.http.GET

interface PublicationService {
    @GET("publication/")
    suspend fun getAllPosts(): Response<List<Publication>>
}
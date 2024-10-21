package com.example.olimpo_app.listeners

import com.example.olimpo_app.data.models.Publication
import retrofit2.Response

interface PublicationRepository {

    suspend fun getAllPosts(): Response<List<Publication>>
}
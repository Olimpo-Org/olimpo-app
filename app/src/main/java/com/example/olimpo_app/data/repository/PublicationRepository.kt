package com.example.olimpo_app.data.repository

import com.example.olimpo_app.data.model.feedFlow.Publication
import com.example.olimpo_app.data.network.FeaturesAPIService
import retrofit2.Response

class PublicationRepository(private val service: FeaturesAPIService) {
    suspend fun getAllPosts(): Response<List<Publication>> {
        return service.getAllPosts()
    }

}

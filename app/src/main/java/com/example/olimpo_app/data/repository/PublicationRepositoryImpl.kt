package com.example.olimpo_app.data.repository

import com.example.olimpo_app.data.models.Publication
import com.example.olimpo_app.listeners.PublicationRepository
import com.example.olimpo_app.listeners.PublicationService
import retrofit2.Response

class PublicationRepositoryImpl(private val service: PublicationService): PublicationRepository{
    override suspend fun getAllPosts(): Response<List<Publication>> {
        return service.getAllPosts()
    }

}

package com.example.olimpo_app.presentation.usecase

import com.example.olimpo_app.data.models.Publication
import com.example.olimpo_app.listeners.PublicationRepository
import retrofit2.Response

class PublicationUseCase (
    private val repository: PublicationRepository
){

    suspend fun getAllPosts(): Response<List<Publication>> {
        try {
            return repository.getAllPosts()
        } catch (e: Exception) {
            throw e
        }
    }
}
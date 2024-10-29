package com.example.olimpo_app.data.repository

import com.example.olimpo_app.data.model.feedFlow.Publication
import com.example.olimpo_app.data.network.FeaturesAPIService
import retrofit2.Response

class PublicationRepository(private val service: FeaturesAPIService) {
    suspend fun getAllPosts(): Response<List<Publication>> {
        return service.getAllPosts()
    }
    suspend fun createPublication(publication: Publication): Response<Publication>{
        return service.createPublication(publication)
    }
    suspend fun getPublicationsByCommunity(communityId: String): Response<List<Object>>{
        return service.getPublicationsByCommunity(communityId)
    }
    suspend fun getPublicationsByCommunityAndUser(communityId: String, userId: String): Response<List<Publication>>{
        return service.getPublicationsByCommunityAndUser(communityId, userId)
    }
    suspend fun likePublication(publicationId: String, userId: String): Response<Unit>{
        return service.likePublication(publicationId, userId)
    }
    suspend fun unlikePublication(publicationId: String, userId: String): Response<Unit>{
        return service.unlikePublication(publicationId, userId)
    }
}

package com.example.olimpo_app.data.repository

import com.example.olimpo_app.data.model.feedFlow.Publication
import com.example.olimpo_app.data.network.FeaturesAPIService
import com.example.olimpo_app.presentation.adapters.FeedItem
import retrofit2.Response

class PublicationRepository(private val service: FeaturesAPIService) {
    suspend fun getAllPublication(): Response<List<Object>> {
        return service.getAllPosts()
    }
    suspend fun createPublication(publication: Publication): Response<Publication>{
        return service.createPublication(publication)
    }
    suspend fun getPublicationsByCommunity(communityId: String): Response<List<FeedItem>>{
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

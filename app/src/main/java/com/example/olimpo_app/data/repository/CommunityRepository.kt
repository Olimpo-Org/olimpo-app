package com.example.olimpo_app.data.repository

import com.example.olimpo_app.data.model.accessFlow.CommunityAPI
import com.example.olimpo_app.data.model.accessFlow.Solicitation
import com.example.olimpo_app.data.model.accessFlow.UserAPI
import com.example.olimpo_app.data.network.AccessAPIService
import retrofit2.Response
import java.util.UUID

class CommunityRepository(
    private val service: AccessAPIService
) {
    suspend fun createCommunity(community: CommunityAPI): Response<CommunityAPI> {
        return service.createCommunity(community)
    }

    suspend fun getAllCommunities(): Response<List<CommunityAPI>> {
        return service.getAllCommunities()
    }

    suspend fun getAllUsersInCommunity(communityId: UUID): Response<List<UserAPI>> {
        return service.getAllUsersInCommunity(communityId)
    }

    suspend fun getAllCommunitiesByUser(userId: UUID): Response<List<CommunityAPI>> {
        return service.getAllCommunitiesByUser(userId)
    }

    suspend fun createSolicitation(solicitation: Solicitation): Response<Solicitation> {
        return service.createSolicitation(solicitation)
    }

    suspend fun getAllSolicitations(communityId: UUID): Response<List<Solicitation>> {
        return service.getAllSolicitations(communityId)
    }

    suspend fun acceptSolicitation(solicitationId: Long): Response<String> {
        return service.acceptSolicitation(solicitationId)
    }
}
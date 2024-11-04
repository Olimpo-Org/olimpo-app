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
    suspend fun createCommunity(community: CommunityAPI, userCpf: String): Response<CommunityAPI> {
        return service.createCommunity(community, userCpf)
    }

    suspend fun getAllCommunities(): Response<List<CommunityAPI>> {
        return service.getAllCommunities()
    }

    suspend fun getAllUsersInCommunity(communityId: Int): Response<List<UserAPI>> {
        return service.getAllUsersInCommunity(communityId)
    }

    suspend fun getAllCommunitiesByUser(userId: Int): Response<List<CommunityAPI>> {
        return service.getAllCommunitiesByUser(userId)
    }

    suspend fun createSolicitation(solicitation: Solicitation): Response<Solicitation> {
        return service.createSolicitation(solicitation)
    }

    suspend fun getAllSolicitationsByUser(customerId: Int): Response<List<Solicitation>> {
        return service.getAllSolicitationsByUser(customerId)
    }

    suspend fun acceptSolicitation(solicitationId: UUID): Response<String> {
        return service.acceptSolicitation(solicitationId)
    }

    suspend fun rejectSolicitation(solicitationId: UUID): Response<String> {
        return service.rejectSolicitation(solicitationId)
    }
}
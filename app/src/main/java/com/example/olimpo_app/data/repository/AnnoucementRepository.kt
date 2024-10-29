package com.example.olimpo_app.data.repository

import com.example.olimpo_app.data.model.negociationFlow.AnnouncementAPI
import com.example.olimpo_app.data.network.FeaturesAPIService
import retrofit2.Response

class AnnoucementRepository(
    private val service: FeaturesAPIService
) {
    suspend fun createAnnouncement(announcement: AnnouncementAPI): Response<AnnouncementAPI>{
        return service.createAnnouncement(announcement)
    }
    suspend fun getServiceAnnouncementsByCommunity(communityId: String): Response<List<AnnouncementAPI>>{
        return service.getServiceAnnouncementsByCommunity(communityId)
    }
    suspend fun getAnnouncementsByCommunityAndUser(communityId: String, userId: String): Response<List<AnnouncementAPI>>{
        return service.getAnnouncementsByCommunityAndUser(communityId, userId)
    }
    suspend fun getSalesAnnouncementsByCommunity(communityId: String): Response<List<AnnouncementAPI>>{
        return service.getSalesAnnouncementsByCommunity(communityId)
    }
    suspend fun getDonationsAnnouncementsByCommunity(communityId: String): Response<List<AnnouncementAPI>>{
        return service.getDonationsAnnouncementsByCommunity(communityId)
    }

}
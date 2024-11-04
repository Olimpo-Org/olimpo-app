package com.example.olimpo_app.data.repository

import com.example.olimpo_app.data.model.messageFlow.ChatAPI
import com.example.olimpo_app.data.network.FeaturesAPIService
import retrofit2.Response

class ChatRepository(
    private val service: FeaturesAPIService
) {
    suspend fun createChat(chat: ChatAPI): Response<ChatAPI> {
        return service.createChat(chat)
    }
    suspend fun getAllChats(): Response<List<ChatAPI>>{
        return service.getAllChats()
    }
    suspend fun getChatsByCommunity(communityId: String): Response<List<ChatAPI>>{
        return service.getChatsByCommunity(communityId)
    }
    suspend fun getChatsByCommunityAndUser(communityId: String, userId: String): Response<List<ChatAPI>>{
        return service.getChatsByCommunityAndUser(communityId, userId)
    }
    suspend fun addUserToChat(chatId: String, userId: String): Response<Unit>{
        return service.addUserToChat(chatId, userId)
    }
    suspend fun getChatById(chatId: String): Response<ChatAPI>{
        return service.getChatById(chatId)
    }
}
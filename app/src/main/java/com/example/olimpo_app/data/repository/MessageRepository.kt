package com.example.olimpo_app.data.repository

import com.example.olimpo_app.data.model.messageFlow.MessageAPI
import com.example.olimpo_app.data.network.FeaturesAPIService
import retrofit2.Response

class MessageRepository(
    private val service: FeaturesAPIService
) {
    suspend fun createMessage(message: MessageAPI): Response<MessageAPI> {
        return service.createMessage(message)
    }
    suspend fun getMessagesByChat(chatId: String): Response<List<MessageAPI>>{
        return service.getMessagesByChat(chatId)
    }
}
package com.example.olimpo_app.data.model.messageFlow

import java.util.Date

data class ChatAPI(
    val chatId: String,
    val communityId: String,
    val usersIds: List<String>,
    val chatName: String,
    val chatOwners: List<String>,
    val channelType: String,
    val createdAt: Date
)

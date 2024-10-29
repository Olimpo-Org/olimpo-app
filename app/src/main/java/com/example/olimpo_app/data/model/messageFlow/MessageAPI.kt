package com.example.olimpo_app.data.model.messageFlow

import java.util.Date

data class MessageAPI(
    val messageId: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val sentAt: Date
)
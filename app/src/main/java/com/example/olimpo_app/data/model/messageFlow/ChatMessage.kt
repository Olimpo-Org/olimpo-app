package com.example.olimpo_app.data.model.messageFlow

import java.util.Date

data class ChatMessage(
    var senderId: String? = null,
    var receiverId: String? = null,
    var message: String? = null,
    val dateTime: String? = null,
    var dataObject: Date? = null,
    var conversionId: String? = null,
    var conversionName: String? = null,
    var conversionImage: String? = null
)
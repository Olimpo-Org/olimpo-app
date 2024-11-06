package com.example.olimpo_app.data.model.negociationFlow

import java.util.Date

data class AnnouncementAPI(
    val announcementId: String?,
    val communityId: String,
    val senderId: String,
    val senderName: String,
    val images: List<String>,
    val description: String,
    val type: String,
    val sentAt: Date?
)

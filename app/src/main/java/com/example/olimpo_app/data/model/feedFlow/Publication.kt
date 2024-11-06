package com.example.olimpo_app.data.model.feedFlow

data class Publication(
    val publicationId: String? = null,
    val communityId: String,
    val senderId: String,
    val senderName: String,
    val images: List<String>,
    val description: String,
    val likes: MutableList<String>? = mutableListOf(),
)
package com.example.olimpo_app.data.model.feedFlow

data class CommentAPI(
    val commentId: String,
    val publicationId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val senderImage: String
)
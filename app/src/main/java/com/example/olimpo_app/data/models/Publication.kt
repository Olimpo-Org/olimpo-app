package com.example.olimpo_app.data.models

data class Publication(
    var id: String?,
    var community_id: String?,
    var sender_id: String?,
    var sender_image: String,
    var sender_name: String,
    var images: String? = null,
    var description: String?,
    var likes: String,
    var tag: String?
)

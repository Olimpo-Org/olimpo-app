package com.example.olimpo_app.data.models

import androidx.recyclerview.widget.RecyclerView

data class Publication(
    var id: String?,
    var community_id: String?,
    var sender_id: String?,
    var sender_image: Int,
    var sender_name: String,
    var images: RecyclerView.Adapter<RecyclerView.ViewHolder>,
    var description: String?,
    var likes: String,
    var tag: String?
)

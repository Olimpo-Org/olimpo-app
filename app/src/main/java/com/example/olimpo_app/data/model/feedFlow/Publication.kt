package com.example.olimpo_app.data.model.feedFlow

import androidx.recyclerview.widget.RecyclerView

data class Publication(
    var id: String?,
    var communityId: String?,
    var senderId: String?,
    var senderImage: Int,
    var senderName: String,
    var images: RecyclerView.Adapter<RecyclerView.ViewHolder>,
    var description: String?,
    var likes: String,
    var tag: String?
)

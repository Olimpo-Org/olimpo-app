package com.example.olimpo_app.data.model.accessFlow

import java.sql.Date
import java.util.UUID

data class CommunityAPI (
    val id: UUID? = null,
    val name: String,
    val startDate: Date,
    val neighborhood: String,
    val imageUrl: String
)
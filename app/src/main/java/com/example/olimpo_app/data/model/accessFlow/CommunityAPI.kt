package com.example.olimpo_app.data.model.accessFlow

import java.sql.Date

data class CommunityAPI (
    val id: Long? = null,
    val name: String,
    val startDate: Date,
    val neighborhood: String,
    val imageUrl: String
)
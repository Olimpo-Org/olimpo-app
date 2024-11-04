package com.example.olimpo_app.data.model.accessFlow

import java.sql.Date

data class CommunityAPI (
    val id: Int? = null,
    val name: String,
    val startDate: String? = null,
    val neighborhood: String,
    val imageUrl: String
)
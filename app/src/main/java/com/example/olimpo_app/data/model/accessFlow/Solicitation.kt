package com.example.olimpo_app.data.model.accessFlow

import java.util.UUID

data class Solicitation(
    val id: UUID? = null,
    val communityId: UUID,
    val userId: UUID,
    val userName: String,
    val userUrlImage: String
)


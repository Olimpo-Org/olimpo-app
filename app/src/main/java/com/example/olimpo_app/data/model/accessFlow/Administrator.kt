package com.example.olimpo_app.data.model.accessFlow

import java.util.UUID

data class Administrator(
    val id: Long? = null,
    val customerCpf: String,
    val communityId: UUID
)

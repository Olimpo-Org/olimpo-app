package com.example.olimpo_app.data.model.accessFlow

import java.util.UUID

data class UserAPI (
    val id: UUID? = null,
    val email: String,
    val password: String,
    val name: String,
    val surname: String,
    val cpf: String,
    val profileImage: String,
    val genderId: Int
)

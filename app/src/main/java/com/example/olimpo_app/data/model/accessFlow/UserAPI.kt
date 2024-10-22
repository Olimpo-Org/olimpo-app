package com.example.olimpo_app.data.model.accessFlow

data class UserAPI (
    val id: Long? = null,
    val email: String,
    val password: String,
    val name: String,
    val surname: String,
    val cpf: String,
    val profileImage: String,
    val genderId: Int
)

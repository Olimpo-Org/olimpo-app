package com.example.olimpo_app.models

import java.io.Serializable

data class User(
    var name: String?,
    var image: String? = null,
    val email: String? = null,
    var token: String?,
    var id: String
): Serializable
package com.example.olimpo_app.listeners

import com.example.olimpo_app.data.models.User

interface UsersCallback {
    fun onUsersLoaded(users: List<User>)
    fun onError(message: String)
}
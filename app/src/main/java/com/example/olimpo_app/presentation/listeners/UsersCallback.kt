package com.example.olimpo_app.presentation.listeners

import com.example.olimpo_app.data.model.accessFlow.User

interface UsersCallback {
    fun onUsersLoaded(users: List<User>)
    fun onError(message: String)
}
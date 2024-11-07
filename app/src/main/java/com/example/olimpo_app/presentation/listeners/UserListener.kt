package com.example.olimpo_app.presentation.listeners

import com.example.olimpo_app.data.model.accessFlow.User
import com.example.olimpo_app.data.model.accessFlow.UserAPI

interface UserListener {
    fun onUserClicked(user: User, userAPI: UserAPI){

    }
}
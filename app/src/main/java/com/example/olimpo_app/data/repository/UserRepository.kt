package com.example.olimpo_app.data.repository

import com.example.olimpo_app.data.model.accessFlow.Administrator
import com.example.olimpo_app.data.model.accessFlow.Login
import com.example.olimpo_app.data.model.accessFlow.UserAPI
import com.example.olimpo_app.data.network.AccessAPIService
import retrofit2.Response

class UserRepository(
    private val service: AccessAPIService
) {
    suspend fun createUser(user: UserAPI): Response<UserAPI> {
        return service.createUser(user)
    }

    suspend fun login(login: Login): Response<UserAPI> {
        return service.login(login)
    }

    suspend fun getAllUsers(): Response<List<UserAPI>> {
        return service.getAllUsers()
    }
    suspend fun getUserById(userId: Long): Response<UserAPI> {
        return service.getUserById(userId)
    }

    suspend fun userExists(userId: Long): Response<Boolean> {
        return service.userExists(userId)
    }

    suspend fun isAdministrator(userId: String, communityId: Long): Response<Boolean> {
        return service.isAdministrator(userId, communityId)
    }

    suspend fun grantAdministrator(administrator: Administrator): Response<Administrator> {
        return service.grantAdministrator(administrator)
    }

}
package com.example.olimpo_app.data.network

import com.example.olimpo_app.data.model.accessFlow.Administrator
import com.example.olimpo_app.data.model.accessFlow.CommunityAPI
import com.example.olimpo_app.data.model.accessFlow.Login
import com.example.olimpo_app.data.model.accessFlow.Solicitation
import com.example.olimpo_app.data.model.accessFlow.UserAPI
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AccessAPIService {
    // ------------- Community Endpoints -------------//
    @POST("/v1/community/create")
    suspend fun createCommunity(
        @Body community: CommunityAPI
    ): Response<CommunityAPI>

    @GET("/v1/community/getAll")
    suspend fun getAllCommunities(): Response<List<CommunityAPI>>

    @GET("/v1/community/getAllUsersInCommunity/{communityId}")
    suspend fun getAllUsersInCommunity(
        @Path("communityId") communityId: Long
    ): Response<List<UserAPI>>

    @GET("/v1/community/getAllCommunitiesByUser/{userId}")
    suspend fun getAllCommunitiesByUser(
        @Path("userId") userId: Long
    ): Response<List<CommunityAPI>>

    @POST("/v1/community/createSolicitation")
    suspend fun createSolicitation(
        @Body solicitation: Solicitation
    ): Response<Solicitation>

    @GET("/v1/community/getAllSolicitations/{communityId}")
    suspend fun getAllSolicitations(
        @Path("communityId") communityId: Long
    ): Response<List<Solicitation>>

    @POST("/v1/community/acceptSolicitation/{solicitationId}")
    suspend fun acceptSolicitation(
        @Path("solicitationId") solicitationId: Long
    ): Response<String>

    //------------- User Endpoints -------------//
    @POST("/v1/user/create")
    suspend fun createUser(
        @Body user: UserAPI
    ): Response<UserAPI>

    @POST("/v1/user/login")
    suspend fun login(
        @Body login: Login
    ): Response<UserAPI>

    @GET("/v1/user/getAll")
    suspend fun getAllUsers(): Response<List<UserAPI>>

    @GET("/v1/user/getById/{userId}")
    suspend fun getUserById(
        @Path("userId") userId: Long
    ): Response<UserAPI>

    @GET("/v1/user/exists/{userId}")
    suspend fun userExists(
        @Path("userId") userId: Long
    ): Response<Boolean>

    @POST("/v1/user/grantAdministrator")
    suspend fun grantAdministrator(
        @Body administrator: Administrator
    ): Response<Administrator>

    @GET("/v1/user/isAdministrator/{userId}/{communityId}")
    suspend fun isAdministrator(
        @Path("userId") userId: String,
        @Path("communityId") communityId: Long
    ): Response<Boolean>
}
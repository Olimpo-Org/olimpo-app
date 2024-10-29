package com.example.olimpo_app.data.network

import com.example.olimpo_app.data.model.feedFlow.CommentAPI
import com.example.olimpo_app.data.model.feedFlow.Publication
import com.example.olimpo_app.data.model.messageFlow.ChatAPI
import com.example.olimpo_app.data.model.messageFlow.MessageAPI
import com.example.olimpo_app.data.model.negociationFlow.AnnouncementAPI
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FeaturesAPIService {
    @GET("publication/")
    suspend fun getAllPosts(): Response<List<Publication>>


    // Comment Endpoints
    @POST("/v1/comment/create")
    suspend fun createComment(@Body comment: CommentAPI): Response<CommentAPI>

    @GET("/v1/comment/get/{publicationId}")
    suspend fun getCommentsByPublication(@Path("publicationId") publicationId: String): Response<List<CommentAPI>>

    // Publication Endpoints
    @POST("/v1/publication/create")
    suspend fun createPublication(@Body publication: Publication): Response<Publication>

    @GET("/v1/publication/get/{communityId}")
    suspend fun getPublicationsByCommunity(@Path("communityId") communityId: String): Response<List<Object>>

    @GET("/v1/publication/get/{communityId}/{userId}")
    suspend fun getPublicationsByCommunityAndUser(
        @Path("communityId") communityId: String,
        @Path("userId") userId: String
    ): Response<List<Publication>>

    @PATCH("/v1/publication/like/{publicationId}/{userId}")
    suspend fun likePublication(
        @Path("publicationId") publicationId: String,
        @Path("userId") userId: String
    ): Response<Unit>

    @PATCH("/v1/publication/unlike/{publicationId}/{userId}")
    suspend fun unlikePublication(
        @Path("publicationId") publicationId: String,
        @Path("userId") userId: String
    ): Response<Unit>

    // Chat Endpoints
    @POST("/v1/chat/create")
    suspend fun createChat(@Body chat: ChatAPI): Response<ChatAPI>

    @GET("/v1/chat/get")
    suspend fun getAllChats(): Response<List<ChatAPI>>

    @GET("/v1/chat/get/{communityId}")
    suspend fun getChatsByCommunity(@Path("communityId") communityId: String): Response<List<ChatAPI>>

    @GET("/v1/chat/get/{communityId}/{userId}")
    suspend fun getChatsByCommunityAndUser(
        @Path("communityId") communityId: String,
        @Path("userId") userId: String
    ): Response<List<ChatAPI>>

    @PUT("/v1/chat/add/{chatId}/{userId}")
    suspend fun addUserToChat(
        @Path("chatId") chatId: String,
        @Path("userId") userId: String
    ): Response<Unit>

    @GET("/v1/chat/get/{chatId}")
    suspend fun getChatById(@Path("chatId") chatId: String): Response<ChatAPI>

    // Message Endpoints
    @POST("/v1/message/create")
    suspend fun createMessage(@Body message: MessageAPI): Response<MessageAPI>

    @GET("/v1/message/get/{chatId}")
    suspend fun getMessagesByChat(@Path("chatId") chatId: String): Response<List<MessageAPI>>

    // Announcement Endpoints
    @POST("/v1/announcement/create")
    suspend fun createAnnouncement(@Body announcement: AnnouncementAPI): Response<AnnouncementAPI>

    @GET("/v1/announcement/get/services/{communityId}")
    suspend fun getServiceAnnouncementsByCommunity(@Path("communityId") communityId: String): Response<List<AnnouncementAPI>>

    @GET("/v1/announcement/get/{communityId}/{userId}")
    suspend fun getAnnouncementsByCommunityAndUser(
        @Path("communityId") communityId: String,
        @Path("userId") userId: String
    ): Response<List<AnnouncementAPI>>

    @GET("/v1/announcement/get/sales/{communityId}")
    suspend fun getSalesAnnouncementsByCommunity(@Path("communityId") communityId: String): Response<List<AnnouncementAPI>>

    @GET("/v1/announcement/get/donations/{communityId}")
    suspend fun getDonationsAnnouncementsByCommunity(@Path("communityId") communityId: String): Response<List<AnnouncementAPI>>
}

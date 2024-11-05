package com.example.olimpo_app.data.repository

import com.example.olimpo_app.data.model.feedFlow.CommentAPI
import com.example.olimpo_app.data.network.FeaturesAPIService
import retrofit2.Response

class CommentRepository(
    private val service: FeaturesAPIService
) {
    suspend fun createComment(comment: CommentAPI): Response<CommentAPI> {
        return service.createComment(comment)
    }
    suspend fun getCommentsByPublication(publicationId: String): Response<List<CommentAPI>>{
        return service.getCommentsByPublication(publicationId)
    }
}
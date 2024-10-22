package com.moh.journeytest.network

import com.moh.journeytest.model.Comment
import com.moh.journeytest.model.Post
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PostsApiService {
    @GET("posts")
    suspend fun getPosts(): List<Post>

    @GET("comments")
    suspend fun getComments(
        @Query("postId") postId: Int
    ): List<Comment>

    @GET("comments")
    suspend fun getAllComments(): List<Comment>
}

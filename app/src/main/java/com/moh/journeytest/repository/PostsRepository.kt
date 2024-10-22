package com.moh.journeytest.repository

import com.moh.journeytest.data.CommentDao
import com.moh.journeytest.data.PostDao
import com.moh.journeytest.model.Comment
import com.moh.journeytest.model.Post
import com.moh.journeytest.network.PostsApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostsRepository @Inject constructor(
    private val apiService: PostsApiService,
    private val postDao: PostDao, private val commentDao: CommentDao
) {

    // Fetch from API and save to database
    suspend fun refreshPosts(): List<Post> {
        val posts = apiService.getPosts()
        postDao.clearPosts()  // Clear existing posts
        postDao.insertPosts(posts)  // Insert new posts
        return posts
    }

    // Get from local database
    fun getPostsFromDatabase(): List<Post> {
        return postDao.getPosts()
    }

    // Fetch comments of the post from API and save to database
    suspend fun getComments(postId : Int): List<Comment> {
        val comments = apiService.getComments(postId)
        if(comments.isNotEmpty()) {
            commentDao.clearComments(postId)  // Clear existing comments
            commentDao.insertComments(comments)  // Insert new comments
        }
        return comments
    }

    // Fetch comments of the post from API and save to database
    suspend fun getAllComments(): List<Comment> {
        val comments = apiService.getAllComments()
        if(comments.isNotEmpty()) {
            commentDao.clearAllComments()  // Clear existing comments
            commentDao.insertComments(comments)  // Insert new comments
        }
        return comments
    }

    // Get comments of the post from local database
    fun getCommentsFromDatabase(postId : Int): List<Comment> {
        return commentDao.getComments(postId)
    }

    // Get comments of the post from local database
    fun getAllCommentsFromDatabase(): List<Comment> {
        return commentDao.getAllComments()
    }
}
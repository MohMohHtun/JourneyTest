package com.moh.journeytest.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moh.journeytest.model.Comment

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComments(Comments: List<Comment>)

    @Query("SELECT * FROM comments WHERE postId = :postId")
    fun getComments(postId: Int): List<Comment>

    @Query("DELETE FROM comments WHERE postId = :postId")
    fun clearComments(postId: Int)

    @Query("SELECT * FROM comments")
    fun getAllComments(): List<Comment>

    @Query("DELETE FROM comments")
    fun clearAllComments()
}
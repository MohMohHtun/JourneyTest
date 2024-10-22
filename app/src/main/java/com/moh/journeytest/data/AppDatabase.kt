package com.moh.journeytest.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.moh.journeytest.model.Comment
import com.moh.journeytest.model.Post


@Database(entities = [Post::class, Comment::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
}
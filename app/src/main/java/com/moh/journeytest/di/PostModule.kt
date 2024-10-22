package com.moh.journeytest.di

import android.content.Context
import androidx.room.Room
import com.moh.journeytest.data.AppDatabase
import com.moh.journeytest.data.CommentDao
import com.moh.journeytest.data.PostDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PostModule {
    @Provides
    @Singleton
    fun providePostDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "post_database"
        ).build()
    }

    @Provides
    @Singleton
    fun providePostDao(db: AppDatabase): PostDao {
        return db.postDao()
    }

    @Provides
    @Singleton
    fun provideCommentDao(db: AppDatabase): CommentDao {
        return db.commentDao()
    }
}
package com.moh.journeytest.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    @SerializedName("id")
    val id: Int,
    @ColumnInfo(name = "postId")
    @SerializedName("postId")
    val postId: Int,
    @ColumnInfo(name = "name")
    @SerializedName("name")
    val name: String? = null,
    @ColumnInfo(name = "email")
    @SerializedName("email")
    val email: String? = null,
    @ColumnInfo(name = "body")
    @SerializedName("body")
    val body: String? = null
)

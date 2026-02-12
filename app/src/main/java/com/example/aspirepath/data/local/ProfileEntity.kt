package com.example.aspirepath.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class ProfileEntity(
    @PrimaryKey val uid: String,
    val profilePictureUri: String? = null
)

package com.bala.gittrend.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class ProjectOwnerDetails(
    @PrimaryKey
    @SerializedName("id")
    val ownerId: Long,
    @SerializedName("login")
    val ownerName: String,
    @SerializedName("avatar_url")
    val avatarUrl: String
)

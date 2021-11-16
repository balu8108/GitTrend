package com.bala.gittrend.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProjectInfo(
    @PrimaryKey
    val id: Long,
    val name: String,
    val language: String?,
    val description: String,
    val projectUrl: String,
    val forksCount: Long,
    val starGazersCount: Long,
    val projectOwnerId: Long
)

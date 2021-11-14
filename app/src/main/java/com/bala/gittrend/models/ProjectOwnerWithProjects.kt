package com.bala.gittrend.models

import androidx.room.Embedded
import androidx.room.Relation

data class ProjectOwnerWithProjects(
    @Embedded
    val projectOwnerDetails: ProjectOwnerDetails,
    @Relation(
        parentColumn = "ownerId",
        entityColumn = "projectOwnerId"
    )
    val projectInfo: List<ProjectInfo>
)

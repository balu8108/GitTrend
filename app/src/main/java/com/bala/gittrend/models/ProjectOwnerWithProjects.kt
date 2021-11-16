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
    val projectInfos: List<ProjectInfo>
) {
    fun getProjectListFromProjectOwnerWithProjects(): List<ProjectInfoParsed> {
        val projectLists = mutableListOf<ProjectInfoParsed>()
        for (projectInfo in projectInfos) {
            projectLists.add(
                ProjectInfoParsed(
                    projectInfo.id,
                    projectInfo.name,
                    projectInfo.language,
                    projectInfo.forksCount,
                    projectInfo.starGazersCount,
                    projectOwnerDetails
                )
            )
        }
        return projectLists
    }
}

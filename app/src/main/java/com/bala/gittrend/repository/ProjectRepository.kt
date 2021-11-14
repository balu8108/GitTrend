package com.bala.gittrend.repository

import com.bala.gittrend.GitTrendApplication
import com.bala.gittrend.apiservice.ApiService
import com.bala.gittrend.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val application: GitTrendApplication,
    private val apiService: ApiService,
    private val projectDao: ProjectDao
) {

    fun fetchTrendingMovies(): Flow<List<ProjectOwnerWithProjects>> {
        application.launch(Dispatchers.IO) {
            val result = apiService.fetchTrendingGitRepos()
            if (result.isSuccess) {
                result.getOrNull()?.let { projectInfoParsedList ->
                    projectDao.deleteProjectInfos()
                    projectDao.deleteProjectOwnerDetails()
                    val projectInfos = withContext(Dispatchers.Default) {
                        getProjectInfos(projectInfoParsedList)
                    }
                    projectDao.insertProjectInfos(projectInfos)

                    val projectOwners = withContext(Dispatchers.Default) {
                        getProjectOwners(projectInfoParsedList)
                    }
                    projectDao.insertProjectOwnerDetails(projectOwners)
                }
            }
        }
        return fetchTrendingProjectsCached()
    }

    private fun fetchTrendingProjectsCached(): Flow<List<ProjectOwnerWithProjects>> {
        return projectDao.getProjectOwnerWithProjects()/*.map {
            Result.success(it)
        }*/
    }

    private fun getProjectInfos(projectInfoParsedList: ProjectInfoParsedList): List<ProjectInfo> {
        val projectInfos = mutableListOf<ProjectInfo>()
        for (projectInfoParsed in projectInfoParsedList.projectInfos) {
            projectInfos.add(getProjectInfoFromProjectInfoParsed(projectInfoParsed))
        }

        return projectInfos
    }

    private fun getProjectInfoFromProjectInfoParsed(projectInfoParsed: ProjectInfoParsed): ProjectInfo {
        return ProjectInfo(
            projectInfoParsed.id,
            projectInfoParsed.name,
            projectInfoParsed.language,
            projectInfoParsed.forksCount,
            projectInfoParsed.starGazersCount,
            projectInfoParsed.projectOwnerDetails.ownerId
        )
    }

    private fun getProjectOwners(projectInfoParsedList: ProjectInfoParsedList): List<ProjectOwnerDetails> {
        val projectOwners = mutableListOf<ProjectOwnerDetails>()
        for (projectInfoParsed in projectInfoParsedList.projectInfos) {
            projectOwners.add(projectInfoParsed.projectOwnerDetails)
        }

        return projectOwners
    }
}
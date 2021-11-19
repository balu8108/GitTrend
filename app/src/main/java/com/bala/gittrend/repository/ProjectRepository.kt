package com.bala.gittrend.repository

import androidx.datastore.preferences.core.edit
import com.bala.gittrend.GitTrendApplication
import com.bala.gittrend.apiservice.ApiService
import com.bala.gittrend.datastore.DataStorePreferenceKeys
import com.bala.gittrend.models.*
import com.bala.gittrend.utils.kotlinExtensionUtils.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val application: GitTrendApplication,
    private val apiService: ApiService,
    private val projectDao: ProjectDao
) {

    fun fetchTrendingProjects(): Flow<Pair<Boolean, List<ProjectOwnerWithProjects>>> {
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
                    application.dataStore.edit { dataStore ->
                        dataStore[DataStorePreferenceKeys.LAST_SUCCESS_API_CALL] =
                            System.currentTimeMillis()
                    }
                }
            } else {
                application.dataStore.data.map { preferences ->
                    preferences[DataStorePreferenceKeys.LAST_SUCCESS_API_CALL] ?: -1
                }.flatMapLatest { lastSuccessAppiCallTimeStamp ->
                    val isCacheExpired =
                        if (lastSuccessAppiCallTimeStamp == -1L) false else ((System.currentTimeMillis() - lastSuccessAppiCallTimeStamp) > CACHE_EXPIRY_TIME)
                    if (isCacheExpired) {
                        flow<Pair<Boolean, List<ProjectOwnerWithProjects>>> {
                            emit(Pair(false, emptyList()))
                        }
                    } else {
                        fetchTrendingProjectsCached()
                    }
                }
            }
        }
        return fetchTrendingProjectsCached()
    }

    private fun fetchTrendingProjectsCached(): Flow<Pair<Boolean, List<ProjectOwnerWithProjects>>> {
        return application.dataStore.data.map { preferences ->
            preferences[DataStorePreferenceKeys.LAST_SUCCESS_API_CALL] ?: -1
        }.flatMapLatest { lastSuccessAppiCallTimeStamp ->
            val isCacheExpired =
                if (lastSuccessAppiCallTimeStamp == -1L) false else ((System.currentTimeMillis() - lastSuccessAppiCallTimeStamp) > CACHE_EXPIRY_TIME)
            if (isCacheExpired) {
                flow<Pair<Boolean, List<ProjectOwnerWithProjects>>> {
                    emit(Pair(false, emptyList()))
                }
            } else {
                projectDao.getProjectOwnerWithProjects().map {
                    Pair(true, it)
                }
            }
        }
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
            projectInfoParsed.description,
            projectInfoParsed.projectUrl,
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

    companion object {
        private const val CACHE_EXPIRY_TIME = 2 * 60 * 1000L
    }
}
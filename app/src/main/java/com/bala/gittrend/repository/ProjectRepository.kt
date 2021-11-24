package com.bala.gittrend.repository

import androidx.datastore.preferences.core.edit
import com.bala.gittrend.GitTrendApplication
import com.bala.gittrend.apiservice.ApiService
import com.bala.gittrend.datastore.DataStorePreferenceKeys
import com.bala.gittrend.models.*
import com.bala.gittrend.utils.kotlinExtensionUtils.dataStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val application: GitTrendApplication,
    private val apiService: ApiService,
    private val projectDao: ProjectDao
) {

    fun fetchTrendingProjects(): Flow<Pair<ApiCallStatus, List<ProjectOwnerWithProjects>>> {
        runBlocking {
            resetLastSuccessApiCallIfNecessary()
        }
        makeTrendingProjectsApiCall()
        return fetchTrendingProjectsCached()
    }

    fun makeTrendingProjectsApiCall() {
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
                setLastSuccessApiCallIfNecessaryToFailed()
            }
        }
    }

    suspend fun resetLastSuccessApiCallIfNecessary() {
        coroutineScope {
            withContext(coroutineContext)
            {
                val lastSuccessAppiCallTimeStamp = application.dataStore.data.map { preferences ->
                    preferences[DataStorePreferenceKeys.LAST_SUCCESS_API_CALL] ?: -1
                }.first()
                if (hasLastCallFailed(lastSuccessAppiCallTimeStamp)) {
                    application.dataStore.edit { dataStore ->
                        dataStore[DataStorePreferenceKeys.LAST_SUCCESS_API_CALL] = -1
                    }
                }
            }
        }
    }

    private suspend fun setLastSuccessApiCallIfNecessaryToFailed() {
        coroutineScope {
            withContext(coroutineContext)
            {
                val lastSuccessAppiCallTimeStamp = application.dataStore.data.map { preferences ->
                    preferences[DataStorePreferenceKeys.LAST_SUCCESS_API_CALL] ?: -1
                }.first()
                if (isLastSuccessApiCallExpired(lastSuccessAppiCallTimeStamp)) {
                    application.dataStore.edit { dataStore ->
                        dataStore[DataStorePreferenceKeys.LAST_SUCCESS_API_CALL] = -2
                    }
                }
            }
        }
    }

    private fun hasLastCallFailed(lastSuccessAppiCallTimeStamp: Long): Boolean {
        return lastSuccessAppiCallTimeStamp == -2L
    }

    private fun isLastSuccessApiCallExpired(lastSuccessAppiCallTimeStamp: Long): Boolean {
        return if (lastSuccessAppiCallTimeStamp == -1L) true else ((System.currentTimeMillis() - lastSuccessAppiCallTimeStamp) > CACHE_EXPIRY_TIME)
    }

    private fun fetchTrendingProjectsCached(): Flow<Pair<ApiCallStatus, List<ProjectOwnerWithProjects>>> {
        return application.dataStore.data.map { preferences ->
            preferences[DataStorePreferenceKeys.LAST_SUCCESS_API_CALL] ?: -1
        }.flatMapLatest { lastSuccessAppiCallTimeStamp ->
            if (lastSuccessAppiCallTimeStamp == -2L) {
                flowBuilderWithApiCallStatus(ApiCallStatus.FAILED)
            } else if (isLastSuccessApiCallExpired(lastSuccessAppiCallTimeStamp)) {
                flowBuilderWithApiCallStatus(ApiCallStatus.LOADING)
            } else {
                getProjectItemsMappedToSuccess()
            }
        }
    }

    private fun flowBuilderWithApiCallStatus(apiCallStatus: ApiCallStatus): Flow<Pair<ApiCallStatus, List<ProjectOwnerWithProjects>>> {
        return flow<Pair<ApiCallStatus, List<ProjectOwnerWithProjects>>> {
            emit(Pair(apiCallStatus, emptyList()))
        }.flowOn(Dispatchers.IO)
    }

    private fun getProjectItemsMappedToSuccess(): Flow<Pair<ApiCallStatus, List<ProjectOwnerWithProjects>>> {
        return projectDao.getProjectOwnerWithProjects().map {
            Pair(ApiCallStatus.SUCCESS, it)
        }.flowOn(Dispatchers.IO)
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
        private const val CACHE_EXPIRY_TIME = 0.5 * 60 * 1000L
    }
}
package com.bala.gittrend

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bala.gittrend.models.ProjectInfoParsed
import com.bala.gittrend.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    val projectListWithResultStatus by lazy {
        projectRepository.fetchTrendingProjects().map { projectOwnerWithProjectsWithResult ->
            val resultStatus = projectOwnerWithProjectsWithResult.first
            val projectOwnerWithProjects = projectOwnerWithProjectsWithResult.second
            val projectList = mutableListOf<ProjectInfoParsed>()
            for (projectOwnerWithProject in projectOwnerWithProjects) {
                projectList.addAll(projectOwnerWithProject.getProjectListFromProjectOwnerWithProjects())
            }
            Pair(resultStatus, projectList.toList())
        }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
    }

    fun onRetry() {
        runBlocking {
            projectRepository.resetLastSuccessApiCallIfNecessary()
        }
        projectRepository.makeTrendingProjectsApiCall()
    }
}

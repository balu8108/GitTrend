package com.bala.gittrend

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.bala.gittrend.models.ProjectInfoParsed
import com.bala.gittrend.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val sortMethod by lazy {
        savedStateHandle.getLiveData(SORT_METHOD, SortMethod.SORT_BY_NAME).asFlow()
    }

    val projectListWithResultStatus by lazy {
        sortMethod.flatMapLatest { sortMethod ->
            projectRepository.fetchTrendingProjects().map { projectOwnerWithProjectsWithResult ->
                val resultStatus = projectOwnerWithProjectsWithResult.first
                val projectOwnerWithProjects = projectOwnerWithProjectsWithResult.second
                var projectList = mutableListOf<ProjectInfoParsed>()
                for (projectOwnerWithProject in projectOwnerWithProjects) {
                    projectList.addAll(projectOwnerWithProject.getProjectListFromProjectOwnerWithProjects())
                }
                Pair(resultStatus, sortProjectList(projectList, sortMethod))
            }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
        }
    }

    private fun sortProjectList(
        projectList: List<ProjectInfoParsed>,
        sortMethod: SortMethod
    ): List<ProjectInfoParsed> {
        if (sortMethod == SortMethod.SORT_BY_NAME) {
            return projectList.sortedBy { projectInfoParsed ->
                projectInfoParsed.name
            }
        } else {
            return projectList.sortedByDescending { projectInfoParsed ->
                projectInfoParsed.starGazersCount
            }
        }
    }

    fun onRetry() {
        runBlocking {
            projectRepository.resetLastSuccessApiCallIfNecessary()
        }
        projectRepository.makeTrendingProjectsApiCall()
    }

    fun setSortMethod(sortMethod: SortMethod) {
        savedStateHandle.set(SORT_METHOD, sortMethod)
    }

    enum class SortMethod {
        SORT_BY_STARS,
        SORT_BY_NAME
    }

    companion object {
        private const val SORT_METHOD = "sort method"
    }
}

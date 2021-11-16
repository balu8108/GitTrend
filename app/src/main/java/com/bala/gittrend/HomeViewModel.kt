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
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    val projectList by lazy {
        projectRepository.fetchTrendingProjects().map { projectOwnerWithProjects ->
            val projectList = mutableListOf<ProjectInfoParsed>()
            for (projectOwnerWithProject in projectOwnerWithProjects) {
                projectList.addAll(projectOwnerWithProject.getProjectListFromProjectOwnerWithProjects())
            }
            // if (projectOwnerWithProjects.isSuccess) {
            /*for (item in projectOwnerWithProjects) {
                Log.d("balatag", ": $item")
            }*/
            //  }
            projectList.toList()
        }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
    }
}

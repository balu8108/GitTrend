package com.bala.gittrend

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bala.gittrend.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            projectRepository.fetchTrendingMovies().collect { result ->
                // if (result.isSuccess) {
                for (item in result) {
                    Log.d("balatag", ": $item")
                }
                //  }
            }
        }
    }
}
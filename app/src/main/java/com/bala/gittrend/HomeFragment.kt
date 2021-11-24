package com.bala.gittrend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bala.gittrend.adapters.ProjectListAdapter
import com.bala.gittrend.databinding.FragmentHomeBinding
import com.bala.gittrend.models.ApiCallStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private val viewBinding
        get() = _viewBinding!!
    private var _viewBinding: FragmentHomeBinding? = null

    @Inject
    lateinit var projectListAdapter: ProjectListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = FragmentHomeBinding.inflate(inflater)
        return _viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        viewBinding.gitRepoList.adapter = projectListAdapter
        setLoadingView()
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED)
            {
                viewModel.projectListWithResultStatus.collect { projectListWithResultStatus ->
                    val status = projectListWithResultStatus.first
                    val projectList = projectListWithResultStatus.second
                    if (status == ApiCallStatus.SUCCESS) {
                        projectListAdapter.submitList(projectList) {
                            if (view != null && projectList.isNotEmpty()) {
                                viewBinding.loadingView.root.isVisible = false
                                viewBinding.gitRepoList.isVisible = true
                            }
                        }
                    } else if (status == ApiCallStatus.FAILED) {
                        viewBinding.loadingView.root.isVisible = false
                        viewBinding.errorScreen.root.isVisible = true
                    }
                }
            }
        }

        viewBinding.errorScreen.retryButton.setOnClickListener {
            setLoadingView()
            viewModel.onRetry()
        }
    }

    private fun setLoadingView() {
        viewBinding.loadingView.root.isVisible = true
        viewBinding.gitRepoList.isVisible = false
        viewBinding.errorScreen.root.isVisible = false
    }
}
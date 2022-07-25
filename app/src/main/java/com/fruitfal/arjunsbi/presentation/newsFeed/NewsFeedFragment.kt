package com.fruitfal.arjunsbi.presentation.newsFeed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.fruitfal.arjunsbi.core.utils.showSnackBar
import com.fruitfal.arjunsbi.core.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.fruitfal.arjunsbi.databinding.FragmentNewsFeedBinding as B


@AndroidEntryPoint
class NewsFeedFragment : Fragment() {

    // Binding, ViewModel
    private var _binding: B? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<NewsFeedViewModel>()

    // View variables
    private val adapter = TopHeadlinesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getTopHeadlines()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = B.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.setupRecyclerView()
        consumeUiState()
    }

    private fun B.setupRecyclerView() {
        // Set up recyclerview with adapter
        recyclerViewTopHeadlines.layoutManager = LinearLayoutManager(context)
        recyclerViewTopHeadlines.adapter = adapter
    }

    private fun consumeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.newsFeedUiState.collect {

                        // Progress Loading
                        showProgressBar(it.isLoading)

                        // Error Message
                        it.errorMessage?.let { it1 ->
                            showSnackBar(it1)
                            viewModel.errorMessageShown()
                        }

                        // Successful Message
                        it.message?.let { it1 ->
                            showToast(it1)
                            viewModel.messageShown()
                        }

                        // Data
                        adapter.addData(it.headlineItems)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // UI Related Helper
    private fun showProgressBar(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
    }

}
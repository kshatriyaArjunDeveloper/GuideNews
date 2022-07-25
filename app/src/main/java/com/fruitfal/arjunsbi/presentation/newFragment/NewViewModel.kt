package com.fruitfal.arjunsbi.presentation.newFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fruitfal.arjunsbi.core.utils.AppConstants.LOG_TAG
import com.fruitfal.arjunsbi.core.utils.Result
import com.fruitfal.arjunsbi.core.utils.Result.Error
import com.fruitfal.arjunsbi.core.utils.Result.Success
import com.fruitfal.arjunsbi.data.models.TopHeadlinesApiResponseModel
import com.fruitfal.arjunsbi.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

data class TopHeadlinesUiState(
    val headlineItems: List<TopHeadlineItemUiState> = listOf(),
    val errorMessage: String? = null,
    val message: String? = null,
    val isLoading: Boolean = false
)

data class TopHeadlineItemUiState(
    val newsSource: String,
    val body: String,
    val time: String,
    val imageUrl: String?,
    val saveNews: (String) -> Unit
)

@HiltViewModel
class NewViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TopHeadlinesUiState())
    val uiState: StateFlow<TopHeadlinesUiState> = _uiState.asStateFlow()

    val job = viewModelScope.launch {
        try {
            showProgressBar(true)

            // Add data
            val result: Result<TopHeadlinesApiResponseModel> = repository.fetchTopHeadlines()
            when (result) {
                is Success -> {
                    val newTopHeadlineList = result.data.articles.map {
                        TopHeadlineItemUiState(
                            it.source.name,
                            it.title,
                            it.time,
                            it.imageUrl,
                            ::saveNews
                        )
                    }
                    _uiState.value = TopHeadlinesUiState(
                        headlineItems = newTopHeadlineList,
                        errorMessage = null,
                        message = result.data.message,
                        isLoading = false
                    )
                }
                is Error -> {
                    _uiState.value = TopHeadlinesUiState(
                        errorMessage = result.exception.message,
                        isLoading = false
                    )
                }
            }

        } catch (ioe: IOException) {
            showProgressBar(false)
            _uiState.update {
                val messages = "I think something went wrong"
                it.copy(errorMessage = messages)
            }
        }
    }


    fun getTopHeadlines() {
        job.start()
        checkJob()
    }

    fun checkJob() {
        Log.d(LOG_TAG,"VM Active : ${job.isActive}, isCancelled : ${job.isCancelled}, isCompleted : ${job.isCompleted}")
    }

    private fun saveNews(title: String) {
        viewModelScope.launch {
            try {

//                throw IOException("Problem with backend")

                // Add data
                _uiState.update {
                    it.copy(message = "$title is saved!")
                }
            } catch (ioe: IOException) {
                _uiState.update {
                    val messages = "I think something went wrong"
                    it.copy(errorMessage = messages)
                }
            }
        }
    }


    // Helper
    private fun showProgressBar(showProgressbar: Boolean) = _uiState.update {
        it.copy(isLoading = showProgressbar)
    }

    fun errorMessageShown() = _uiState.update {
        it.copy(errorMessage = null)
    }

    fun messageShown() = _uiState.update {
        it.copy(message = null)
    }

    override fun onCleared() {
        super.onCleared()
        checkJob()
    }

}
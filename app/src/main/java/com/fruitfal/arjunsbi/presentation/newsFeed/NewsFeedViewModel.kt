package com.fruitfal.arjunsbi.presentation.newsFeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class NewsFeedViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TopHeadlinesUiState())
    val newsFeedUiState: StateFlow<TopHeadlinesUiState> = _uiState.asStateFlow()

    fun getTopHeadlines() {
        viewModelScope.launch {
            try {
                uiStateLoading(true)
                val result = repository.fetchTopHeadlines()
                handleGTH(result)
            } catch (ioe: IOException) {
                uiStateLoading(false)
                uiStateError("Something went wrong")
            }
        }
    }

    private fun handleGTH(result: Result<TopHeadlinesApiResponseModel>) {
        uiStateLoading(false)
        when (result) {
            is Success -> {
                val newTopHeadlineItemUiStateList = result.data.articles.map {
                    TopHeadlineItemUiState(
                        it.source.name,
                        it.title,
                        it.time,
                        it.imageUrl,
                        ::saveNews
                    )
                }
                _uiState.value = TopHeadlinesUiState(
                    headlineItems = newTopHeadlineItemUiStateList,
                    errorMessage = null,
                    message = result.data.message,
                    isLoading = false
                )
            }
            is Error -> {
                uiStateError(result.exception.message)
            }
        }
    }

    private fun saveNews(title: String) {
        viewModelScope.launch {
            try {
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

    // UI STATE HELPER METHODS
    private fun uiStateLoading(showProgressbar: Boolean) = _uiState.update {
        it.copy(isLoading = showProgressbar)
    }

    private fun uiStateError(errorMessage: String?) = _uiState.update {
        it.copy(errorMessage = errorMessage)
    }

    fun errorMessageShown() = _uiState.update {
        it.copy(errorMessage = null)
    }

    fun messageShown() = _uiState.update {
        it.copy(message = null)
    }

}
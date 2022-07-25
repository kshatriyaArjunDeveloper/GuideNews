package com.fruitfal.arjunsbi.data.remoteDataSource

import com.fruitfal.arjunsbi.core.utils.Result
import com.fruitfal.arjunsbi.core.utils.Result.Error
import com.fruitfal.arjunsbi.core.utils.Result.Success
import com.fruitfal.arjunsbi.data.models.TopHeadlinesApiResponseModel
import com.fruitfal.arjunsbi.data.retrofit.API
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRemoteDataSource @Inject constructor(private val newsApi: API) {

    suspend fun fetchTopHeadlinesFromRemote(): Result<TopHeadlinesApiResponseModel> =
        withContext(IO) {
            val response =
                newsApi.fetchNewsTopHeadlines(1, 50, "", "in")

            return@withContext when {
                response.isSuccessful && response.body() != null -> Success(response.body()!!)
                response.code() == 401 -> Error(Exception("You are unauthorized"))
                response.code() == 429 -> Error(Exception("Limit is exceeded. Please wait"))
                else -> Error(Exception("There is some problem"))
            }
        }

}
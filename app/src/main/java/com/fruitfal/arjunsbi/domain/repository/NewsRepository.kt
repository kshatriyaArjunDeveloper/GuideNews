package com.fruitfal.arjunsbi.domain.repository

import com.fruitfal.arjunsbi.core.di.CoroutineModule.ApplicationScope
import com.fruitfal.arjunsbi.core.utils.Result
import com.fruitfal.arjunsbi.core.utils.Result.Success
import com.fruitfal.arjunsbi.data.models.TopHeadlinesApiResponseModel
import com.fruitfal.arjunsbi.data.remoteDataSource.NewsRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    private val remoteDataSource: NewsRemoteDataSource,
    @ApplicationScope private val externalScope: CoroutineScope
) {

    // Mutex to make writes to cached values thread-safe.
    private val latestNewsMutex = Mutex()

    // Cache of the latest news got from the network.
    private var topHeadlinesModelCache: TopHeadlinesApiResponseModel? = null

    suspend fun fetchTopHeadlines(refresh: Boolean = false): Result<TopHeadlinesApiResponseModel> {

        val shouldFetchFromRemote = refresh || topHeadlinesModelCache == null
        return if (shouldFetchFromRemote) {

            externalScope.async {
                val response = remoteDataSource.fetchTopHeadlinesFromRemote()
                response.also {
                    // Cache successfully fetched data
                    latestNewsMutex.withLock {
                        if (it is Success) topHeadlinesModelCache = it.data
                    }
                }
                return@async response
            }.await()

        } else {
            latestNewsMutex.withLock { Success(this.topHeadlinesModelCache!!) }
        }
    }

}
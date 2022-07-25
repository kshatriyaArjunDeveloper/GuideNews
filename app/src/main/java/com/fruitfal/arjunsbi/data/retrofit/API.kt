package com.fruitfal.arjunsbi.data.retrofit

import com.fruitfal.arjunsbi.BuildConfig
import com.fruitfal.arjunsbi.data.models.TopHeadlinesApiResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface API {

    /** To load and search top headlines by different params */
    @GET("top-headlines")
    suspend fun fetchNewsTopHeadlines(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("q") query: String,
        @Query("country") countryCode: String,
        @Header("Authorization") auth: String = BuildConfig.API_KEY,
    ): Response<TopHeadlinesApiResponseModel>

}
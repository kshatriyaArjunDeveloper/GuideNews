package com.fruitfal.arjunsbi.data.models

data class TopHeadlinesApiResponseModel(
    val status: String,
    val code: String?,
    val message: String?,
    val totalResults: Int,
    val articles: List<ArticleModel>
)
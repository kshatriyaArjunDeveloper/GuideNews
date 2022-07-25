package com.fruitfal.arjunsbi.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class ArticleModel(
    val source: SourceModel,
    val author: String?,
    val title: String,
    val description: String?,
    @SerializedName("url")
    val newsUrl: String,
    @SerializedName("urlToImage")
    val imageUrl: String?,
    @SerializedName("publishedAt")
    val time: String,
    val content: String?
) : Parcelable

@Parcelize
data class SourceModel(
    val id: String?,
    val name: String
) : Parcelable

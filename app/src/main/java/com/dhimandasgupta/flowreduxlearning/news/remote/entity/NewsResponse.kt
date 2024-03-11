package com.dhimandasgupta.flowreduxlearning.news.remote.entity

import kotlinx.serialization.Serializable

sealed interface NewsDataState

data class LoadingState(val source: NewsSource) : NewsDataState
data class ErrorState(val throwable: Throwable) : NewsDataState
data class SuccessState(val newsResponse: NewsResponse) : NewsDataState

data class NewsSource(
    val sourceName: String,
)

@Serializable
data class NewsResponse(
    val articles: List<Article> = emptyList(),
    val status: String,
    val totalResults: Int
)

@Serializable
data class Article(
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source,
    val title: String?,
    val url: String?,
    val urlToImage: String?
)

@Serializable
data class Source(
    val id: String?,
    val name: String?
)
package com.dhimandasgupta.flowreduxlearning.news.remote

import com.dhimandasgupta.flowreduxlearning.news.remote.entity.NewsResponse

interface NewsApiService {
    suspend fun getNews(
        query: String
    ): Result<NewsResponse>
}
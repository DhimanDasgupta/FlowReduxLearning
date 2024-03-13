package com.dhimandasgupta.flowreduxlearning.news.remote

import com.dhimandasgupta.flowreduxlearning.news.remote.entity.NewsResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

private const val apiKey = "8aed68a4448b4c50b1c72a0fb83be86a"

class NewsApiServiceImpl @Inject constructor(
    private val client: HttpClient
) : NewsApiService {

    override suspend fun getNews(
        query: String
    ): Result<NewsResponse> = kotlin.runCatching {
        client.get {
            url(HttpRoutes.NEWS)
            parameter("q", query)
            /**
             * // from - yyyy-MM-dd
             * For free Account using this may return empty article list
             * so get a day prior than current date may be more appropriate
             * */
            parameter("from", getDatePriorTenDays())
            parameter("to", getCurrentDate())
            parameter("sortBy", "popularity")
            /**
             * If apiKey is passed as parameter
             * */
            //parameter("apiKey", "add your key here")
            /**
             * If apiKey is passed as header
             * */
            header("x-api-key", apiKey)
        }
    }
}

/**
 * Instant to formatted date
 *
 * Not aware of any DateFormat in kotlinx.datetime as of now
 * */
private fun getCurrentDate(): String {
    val instant = Clock.System.now()
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dateTime.date.year}-${dateTime.date.monthNumber}-${dateTime.date.dayOfMonth}"
}

private fun getDatePriorTenDays(): String {
    val instant = Clock.System.now()
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime().minusDays(10)

    return "${dateTime.year}-${dateTime.monthValue}-${dateTime.dayOfMonth}"
}
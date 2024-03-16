package com.dhimandasgupta.flowreduxlearning.statemachines

import app.cash.turbine.test
import com.dhimandasgupta.flowreduxlearning.news.remote.NewsApiService
import com.dhimandasgupta.flowreduxlearning.news.remote.entity.Article
import com.dhimandasgupta.flowreduxlearning.news.remote.entity.NewsResponse
import com.dhimandasgupta.flowreduxlearning.news.remote.entity.Source
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class NewsSearchStateMachineTest {
    @Test
    fun `test NewsSearchStateMachine Initial State`() = runTest {
        val stateMachine = NewsSearchStateMachine(NewsApiSuccessImpl())

        stateMachine.state.test {
            assertEquals(NoSearchState(""), awaitItem())
        }
    }

    @Test
    fun `test InputSearchAction action with less than 3 characters in NewsSearchStateMachine, when it is in Initial State`() = runTest {
        val stateMachine = NewsSearchStateMachine(NewsApiSuccessImpl())

        stateMachine.state.test {
            stateMachine.dispatch(InputSearchAction("ab"))
            assertEquals(NoSearchState(""), awaitItem())
        }
    }

    @Test
    fun `test InputSearchAction action with greater equal to 3 characters in NewsSearchStateMachine, when it is in Initial State`() = runTest {
        val stateMachine = NewsSearchStateMachine(NewsApiSuccessImpl())

        stateMachine.state.test {
            assertEquals(NoSearchState(""), awaitItem())
            stateMachine.dispatch(InputSearchAction("abc"))
            assertEquals(SearchLoadingState("abc"), awaitItem())
            assertEquals(
                SearchSuccessState(
                    inputSearch = "abc",
                    articles = articles
                ),
                awaitItem()
            )
        }
    }

    @Test
    fun `test InputSearchAction action with less than 3 characters in NewsSearchStateMachine, when it is in SearchSuccessState State`() = runTest {
        val stateMachine = NewsSearchStateMachine(NewsApiSuccessImpl())

        stateMachine.state.test {
            assertEquals(NoSearchState(""), awaitItem())
            stateMachine.dispatch(InputSearchAction("abc"))
            assertEquals(SearchLoadingState("abc"), awaitItem())
            assertEquals(
                SearchSuccessState(
                    inputSearch = "abc",
                    articles = articles
                ),
                awaitItem()
            )
            stateMachine.dispatch(InputSearchAction("ab"))
            assertEquals(NoSearchState(""), awaitItem())
        }
    }

    @Test
    fun `test InputSearchAction action with greater equal to 3 characters in NewsSearchStateMachine, when it is in SearchSuccessState State`() = runTest {
        val stateMachine = NewsSearchStateMachine(NewsApiSuccessImpl())

        stateMachine.state.test {
            assertEquals(NoSearchState(""), awaitItem())
            stateMachine.dispatch(InputSearchAction("abc"))
            assertEquals(SearchLoadingState("abc"), awaitItem())
            assertEquals(
                SearchSuccessState(
                    inputSearch = "abc",
                    articles = articles
                ),
                awaitItem()
            )
            stateMachine.dispatch(InputSearchAction("abcd"))
            assertEquals(SearchLoadingState("abcd"), awaitItem())
            assertEquals(
                SearchSuccessState(
                    inputSearch = "abcd",
                    articles = articles
                ),
                awaitItem()
            )
        }
    }

    @Test
    fun `test InputSearchAction action with less than 3 characters in NewsSearchStateMachine, when it is in Initial State and API fails`() = runTest {
        val stateMachine = NewsSearchStateMachine(NewsApiFailureImpl())

        stateMachine.state.test {
            stateMachine.dispatch(InputSearchAction("ab"))
            assertEquals(NoSearchState(""), awaitItem())
        }
    }

    @Test
    fun `test InputSearchAction action with greater equal to 3 characters in NewsSearchStateMachine, when it is in Initial State and API fails`() = runTest {
        val stateMachine = NewsSearchStateMachine(NewsApiFailureImpl())

        stateMachine.state.test {
            assertEquals(NoSearchState(""), awaitItem())
            stateMachine.dispatch(InputSearchAction("abc"))
            assertEquals(SearchLoadingState("abc"), awaitItem())
            assertEquals(
                SearchFailureState(
                    inputSearch = "abc",
                    zeroResult = false,
                    throwable = exception
                ),
                awaitItem()
            )
        }
    }

    @Test
    fun `test InputSearchAction action with less than 3 characters in NewsSearchStateMachine, when it is in SearchFailureState State`() = runTest {
        val stateMachine = NewsSearchStateMachine(NewsApiFailureImpl())

        stateMachine.state.test {
            assertEquals(NoSearchState(""), awaitItem())
            stateMachine.dispatch(InputSearchAction("abc"))
            assertEquals(SearchLoadingState("abc"), awaitItem())
            assertEquals(
                SearchFailureState(
                    inputSearch = "abc",
                    zeroResult = false,
                    throwable = exception
                ),
                awaitItem()
            )
            stateMachine.dispatch(InputSearchAction("ab"))
            assertEquals(
                SearchFailureState(
                    inputSearch = "ab",
                    zeroResult = false,
                    throwable = exception
                ),
                awaitItem()
            )
            stateMachine.dispatch(InputSearchAction("a"))
            assertEquals(
                SearchFailureState(
                    inputSearch = "a",
                    zeroResult = false,
                    throwable = exception
                ),
                awaitItem()
            )
            stateMachine.dispatch(InputSearchAction(""))
            assertEquals(
                NoSearchState(""),
                awaitItem()
            )
        }
    }
}

private val article = Article(
    author = "Test author",
    content = "Test content",
    description = "Test description",
    publishedAt = "Test publishedAt",
    source = Source(
        id = "Test source id",
        name = "Test source name"
    ),
    title = "Test title",
    url = "Test url",
    urlToImage = "Test urlToImage"
)

private val articles = listOf(article)

private class NewsApiSuccessImpl: NewsApiService {
    override suspend fun getNews(query: String): Result<NewsResponse> = Result.success(
        NewsResponse(
            status = "ok",
            totalResults = 1,
            articles = articles
        )
    )
}

private val exception = Throwable("Test exception")

private class NewsApiFailureImpl: NewsApiService {
    override suspend fun getNews(query: String): Result<NewsResponse> =
        Result.failure(exception)
}
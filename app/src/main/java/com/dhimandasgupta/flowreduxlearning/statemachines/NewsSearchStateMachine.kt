package com.dhimandasgupta.flowreduxlearning.statemachines

import androidx.compose.runtime.Immutable
import com.dhimandasgupta.flowreduxlearning.news.remote.NewsApiService
import com.dhimandasgupta.flowreduxlearning.news.remote.entity.Article
import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import kotlinx.coroutines.ExperimentalCoroutinesApi

// OUTPUT State
@Immutable
sealed interface SearchState

// Derived states from the Output State
data object NoSearchState: SearchState

data class SearchLoadingState(
    val inputSearch: String
): SearchState
data class SearchSuccessState(
    val inputSearch: String,
    val articles : List<Article>
): SearchState

data class SearchFailureState(
    val inputSearch: String,
    val zeroResult: Boolean = false,
    val throwable: Throwable
): SearchState

// INPUT Action
sealed interface SearchAction

// Derived Actions from Input Action
data object ResetSearchAction : SearchAction
data class InputSearchAction(
    val inputSearch: String
) : SearchAction

@OptIn(ExperimentalCoroutinesApi::class)
class NewsSearchStateMachine(
    private val newsApiService: NewsApiService
): FlowReduxStateMachine<SearchState, SearchAction>(initialState = NoSearchState) {
    init {
        spec {
            inState<NoSearchState> {
                onEnter { state ->
                    state.noChange()
                }

                on<InputSearchAction> { action, state ->
                    if (action.inputSearch.length < 3) {
                        state.noChange<NoSearchState>()
                    } else {
                        state.override { SearchLoadingState(action.inputSearch) }
                    }
                }
            }

            inState<SearchLoadingState> {
                onEnter { state ->
                    state.fetchNewsForSearchTerm(
                        newsApiService = newsApiService,
                        searchTerm = state.snapshot.inputSearch
                    )
                }
            }

            inState<SearchSuccessState> {
                onEnter { state ->
                    state.noChange()
                }

                on<ResetSearchAction> { _, state ->
                    state.override { NoSearchState }
                }

                on<InputSearchAction> { action, state ->
                    if (action.inputSearch.length < 3) {
                        state.override { NoSearchState }
                    } else {
                        state.override { SearchLoadingState(inputSearch = action.inputSearch) }
                    }
                }
            }

            inState<SearchFailureState> {
                on<InputSearchAction> { action, state ->
                    when (action.inputSearch.length) {
                        0 -> state.override { NoSearchState }
                        1, 2 -> state.override {
                            state.snapshot.copy(inputSearch = action.inputSearch)
                        }
                        else -> state.override { SearchLoadingState(action.inputSearch) }
                    }
                }
            }
        }
    }
}

private suspend fun State<SearchLoadingState>.fetchNewsForSearchTerm(
    newsApiService: NewsApiService,
    searchTerm: String
) : ChangedState<SearchState> = newsApiService.getNews(searchTerm)
    .fold(
        { newsResponse ->
            if (newsResponse.articles.isNotEmpty()) {
                override {
                    SearchSuccessState(
                        inputSearch = searchTerm,
                        articles = newsResponse.articles
                    )
                }
            } else {
                override {
                    SearchFailureState(
                        inputSearch = searchTerm,
                        zeroResult = true,
                        throwable = Exception("Looks like we found Zero Results for $searchTerm")
                    )
                }
            }
        }, { throwable ->
            override {
                SearchFailureState(
                    inputSearch = searchTerm,
                    zeroResult = false,
                    throwable = throwable
                )
            }
        }
    )


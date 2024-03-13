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
data class InputSearchAction(
    val inputSearch: String
) : SearchAction

@Suppress("UNCHECKED_CAST")
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
                    fetchNewsForSearchTerm(
                        state = state as State<SearchState>,
                        newsApiService = newsApiService,
                        searchTerm = state.snapshot.inputSearch
                    )
                }
            }

            inState<SearchSuccessState> {
                onEnter { state ->
                    state.noChange()
                }

                on<InputSearchAction> { action, state ->
                    if (action.inputSearch.length < 3) {
                        state.override { NoSearchState }
                    } else {
                        fetchNewsForSearchTerm(
                            state = state as State<SearchState>,
                            newsApiService = newsApiService,
                            searchTerm = state.snapshot.inputSearch
                        )
                    }
                }
            }

            inState<SearchFailureState> {
                on<InputSearchAction> { action, state ->
                    when (action.inputSearch.length) {
                        0 -> state.override { NoSearchState }
                        1, 2 -> state.noChange<SearchFailureState>()
                        else -> state.override { SearchLoadingState(action.inputSearch) }
                    }
                }
            }
        }
    }
}

private suspend fun fetchNewsForSearchTerm(
    state: State<SearchState>,
    newsApiService: NewsApiService,
    searchTerm: String
) : ChangedState<SearchState> = when (state.snapshot) {
    is SearchLoadingState, is SearchSuccessState -> newsApiService.getNews(searchTerm)
        .fold(
            { newsResponse ->
                if (newsResponse.articles.isNotEmpty()) {
                    state.override {
                        SearchSuccessState(
                            inputSearch = searchTerm,
                            articles = newsResponse.articles
                        )
                    }
                } else {
                    state.override {
                        SearchFailureState(
                            inputSearch = searchTerm,
                            zeroResult = true,
                            throwable = Exception("Looks like we found Zero Results for $searchTerm")
                        )
                    }
                }
            }, { throwable ->
                state.override {
                    SearchFailureState(
                        inputSearch = searchTerm,
                        zeroResult = false,
                        throwable = throwable
                    )
                }
            }
        )
    else -> state.noChange()
}


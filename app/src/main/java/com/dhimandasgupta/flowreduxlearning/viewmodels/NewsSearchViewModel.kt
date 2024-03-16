package com.dhimandasgupta.flowreduxlearning.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhimandasgupta.flowreduxlearning.statemachines.ActivityAction
import com.dhimandasgupta.flowreduxlearning.statemachines.ActivityState
import com.dhimandasgupta.flowreduxlearning.statemachines.ActivityStateMachine
import com.dhimandasgupta.flowreduxlearning.statemachines.NewsSearchStateMachine
import com.dhimandasgupta.flowreduxlearning.statemachines.NoSearchState
import com.dhimandasgupta.flowreduxlearning.statemachines.SearchAction
import com.dhimandasgupta.flowreduxlearning.statemachines.SearchState
import com.dhimandasgupta.flowreduxlearning.statemachines.UnInitializedActivityState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsSearchViewModel @Inject constructor(
    private val activityStateMachine: ActivityStateMachine,
    private val newsSearchStateMachine: NewsSearchStateMachine
) : ViewModel() {
    private val _newsSearchUIState = MutableStateFlow(defaultNewsSearchUiState)
    val newsSearchUIState = _newsSearchUIState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                activityStateMachine.state,
                newsSearchStateMachine.state
            ) { activityState, searchState ->
                NewsSearchUIState(
                    activityState = activityState,
                    searchState = searchState
                )
            }.flowOn(Dispatchers.IO).collect { newUiState ->
                _newsSearchUIState.update { newUiState }
            }
        }
    }

    fun dispatchAction(action: SearchAction) = viewModelScope.launch {
        newsSearchStateMachine.dispatch(action)
    }

    fun dispatchActivityAction(action: ActivityAction) = viewModelScope.launch {
        activityStateMachine.dispatch(action)
    }
}

data class NewsSearchUIState(
    val activityState: ActivityState,
    val searchState: SearchState
)

val defaultNewsSearchUiState = NewsSearchUIState(
    activityState = UnInitializedActivityState,
    searchState = NoSearchState("")
)
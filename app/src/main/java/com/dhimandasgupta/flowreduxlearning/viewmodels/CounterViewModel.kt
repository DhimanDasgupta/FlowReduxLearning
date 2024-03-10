package com.dhimandasgupta.flowreduxlearning.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhimandasgupta.flowreduxlearning.statemachines.ActivityAction
import com.dhimandasgupta.flowreduxlearning.statemachines.ActivityState
import com.dhimandasgupta.flowreduxlearning.statemachines.ActivityStateMachine
import com.dhimandasgupta.flowreduxlearning.statemachines.CounterAction
import com.dhimandasgupta.flowreduxlearning.statemachines.CounterBaseState
import com.dhimandasgupta.flowreduxlearning.statemachines.CounterStateMachine
import com.dhimandasgupta.flowreduxlearning.statemachines.UnInitializedActivityState
import com.dhimandasgupta.flowreduxlearning.statemachines.UnInitializedCounterState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CounterViewModel @Inject constructor(
    private val activityStateMachine: ActivityStateMachine,
    private val counterStateMachine: CounterStateMachine
) : ViewModel() {
    private val _counterState = MutableStateFlow(defaultCounterUiState)
    val counterState = _counterState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                activityStateMachine.state,
                counterStateMachine.state
            ) { activityState, counterBaseState ->
                CounterUiState(
                    activityState = activityState,
                    counterState = counterBaseState
                )
            }.flowOn(Dispatchers.IO).collect { newCounterState ->
                _counterState.value = newCounterState
            }
        }
    }

    fun dispatchCounterAction(action: CounterAction) = viewModelScope.launch {
        counterStateMachine.dispatch(action)
    }

    fun dispatchActivityAction(action: ActivityAction) = viewModelScope.launch {
        activityStateMachine.dispatch(action)
    }
}

data class CounterUiState(
    val activityState: ActivityState,
    val counterState: CounterBaseState
)

val defaultCounterUiState = CounterUiState(
    activityState = UnInitializedActivityState,
    counterState = UnInitializedCounterState
)
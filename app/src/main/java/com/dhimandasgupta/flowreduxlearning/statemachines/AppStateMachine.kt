package com.dhimandasgupta.flowreduxlearning.statemachines

import android.content.Context
import androidx.compose.runtime.Immutable
import com.dhimandasgupta.flowreduxlearning.common.ConnectionState
import com.dhimandasgupta.flowreduxlearning.common.currentConnectivityState
import com.dhimandasgupta.flowreduxlearning.common.observeConnectivityAsFlow
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import javax.inject.Inject

// OUTPUT State
@Immutable
sealed interface AppState

// Derived states from the Output State
data object UnInitializedState: AppState
data class InitializedState(
    val connectionState: ConnectionState
): AppState

// INPUT Action
sealed interface AppAction

// STATE MACHINE
@OptIn(ExperimentalCoroutinesApi::class)
class AppStateMachine @Inject constructor(
    @ApplicationContext
    private val context: Context
): FlowReduxStateMachine<AppState, AppAction>(initialState = UnInitializedState) {
    init {
        spec {
            // Everything for UnInitialized State
            inState<UnInitializedState> {
                onEnter { state ->
                    state.override { InitializedState(connectionState = context.currentConnectivityState) }
                }
            }
            // Everything for Initialized State
            inState<InitializedState> {
                collectWhileInState(context.observeConnectivityAsFlow()) { valueEmittedFromFlow, state ->
                    state.mutate {
                        InitializedState(
                            connectionState = valueEmittedFromFlow
                        )
                    }
                }
            }
        }
    }
}
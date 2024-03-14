package com.dhimandasgupta.flowreduxlearning.statemachines

import androidx.compose.runtime.Immutable
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay

// OUTPUT State
@Immutable
sealed interface CounterBaseState

// Derived states from the Output State
data object UnInitializedCounterState: CounterBaseState
data class CounterState(val counter: Int = 0, val enabled: Boolean = true): CounterBaseState {
    override fun toString() =
        """
            CounterState(
                counter = $counter,
                enabled = $enabled
            )
        """.trimIndent()
}

// INPUT Action
sealed interface CounterAction

// Derived Actions
data object MoveToCounterState: CounterAction
data object IncrementAction: CounterAction
data object DecrementAction: CounterAction
data object ResetAction: CounterAction
data object DisableAction: CounterAction
data object EnableAction: CounterAction

// STATE MACHINE
@OptIn(ExperimentalCoroutinesApi::class)
class CounterStateMachine:
    FlowReduxStateMachine<CounterBaseState, CounterAction>(initialState = UnInitializedCounterState) {
    init {
        spec {
            inState<UnInitializedCounterState> {
                onEnter { state ->
                    state.noChange()
                }
                on<MoveToCounterState> { _, state ->
                    state.override { CounterState(0) }
                }

                // This Action block will never be processed since this Action Dose not belong to this State
                on<IncrementAction> { _, state ->
                    state.override { CounterState(0) }
                }
                // This Action block will never be processed since this Action Dose not belong to this State
                on<DecrementAction> { _, state ->
                    state.noChange()
                }
            }

            inState<CounterState> {
                onEnter { state ->
                    state.noChange()
                }
                on<IncrementAction> { _, state ->
                    state.mutate {
                        this.copy(counter = state.snapshot.counter + 1)
                    }
                }
                on<DecrementAction> { _, state ->
                    state.mutate {
                        this.copy(counter = counter - 1)
                    }
                }
                on<ResetAction> { _, state ->
                    state.override { UnInitializedCounterState }
                }
                on<EnableAction> { _, state ->
                    delay(1000L)
                    state.mutate {
                        this.copy(enabled = true)
                    }
                }
                on<DisableAction> { _, state ->
                    state.mutate {
                        this.copy(enabled = false)
                    }
                }
            }
        }
    }
}
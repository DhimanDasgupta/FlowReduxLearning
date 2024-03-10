package com.dhimandasgupta.flowreduxlearning.statemachines

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Immutable
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import kotlinx.coroutines.ExperimentalCoroutinesApi

// OUTPUT State
@Immutable
sealed interface ActivityState

// Derived states from the Output State
data object UnInitializedActivityState : ActivityState
data class InitializedActivityState(
    val widthSizeClass: WindowWidthSizeClass
) : ActivityState

// INPUT Action
sealed interface ActivityAction

// Derived Actions
data class WindowSizeChangedAction(
    val widthSizeClass: WindowWidthSizeClass
): ActivityAction


// STATE MACHINE
@OptIn(ExperimentalCoroutinesApi::class)
class ActivityStateMachine:
    FlowReduxStateMachine<ActivityState, ActivityAction>(initialState = UnInitializedActivityState) {
    init {
        spec {
            inState<UnInitializedActivityState> {
                onEnter { state ->
                    // Not changing the State rather waiting for the WindowSizeChangedAction to be fired from UI.
                    state.noChange()
                }

                on<WindowSizeChangedAction> { action, state ->
                    // Overriding the state with proper Window Size, what is called from UI.
                    state.override {
                        InitializedActivityState(
                            widthSizeClass = action.widthSizeClass,
                        )
                    }
                }
            }

            inState<InitializedActivityState> {
                on<WindowSizeChangedAction> { action, state ->
                    state.mutate {
                        InitializedActivityState(
                            widthSizeClass = action.widthSizeClass,
                        )
                    }
                }
            }
        }
    }
}
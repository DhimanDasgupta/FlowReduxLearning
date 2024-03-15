package com.dhimandasgupta.flowreduxlearning.statemachines

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ActivityStateMachineTest {
    @Test
    fun `test ActivityStateMachine Initial State`() = runTest {
        val stateMachine = ActivityStateMachine()

        stateMachine.state.test {
            assertEquals(UnInitializedActivityState, awaitItem())
        }
    }

    @Test
    fun `test ActivityStateMachine, after WindowSizeChangedAction is fired with WindowWidthSizeClass Compact`() = runTest {
        val stateMachine = ActivityStateMachine()

        stateMachine.state.test {
            assertEquals(UnInitializedActivityState, awaitItem())
            stateMachine.dispatch(WindowSizeChangedAction(WindowWidthSizeClass.Compact))
            assertEquals(InitializedActivityState(WindowWidthSizeClass.Compact), awaitItem())
        }
    }

    @Test
    fun `test ActivityStateMachine, after WindowSizeChangedAction is fired with WindowWidthSizeClass Medium`() = runTest {
        val stateMachine = ActivityStateMachine()

        stateMachine.state.test {
            assertEquals(UnInitializedActivityState, awaitItem())
            stateMachine.dispatch(WindowSizeChangedAction(WindowWidthSizeClass.Medium))
            assertEquals(InitializedActivityState(WindowWidthSizeClass.Medium), awaitItem())
        }
    }

    @Test
    fun `test ActivityStateMachine, after WindowSizeChangedAction is fired with WindowWidthSizeClass Expanded`() = runTest {
        val stateMachine = ActivityStateMachine()

        stateMachine.state.test {
            assertEquals(UnInitializedActivityState, awaitItem())
            stateMachine.dispatch(WindowSizeChangedAction(WindowWidthSizeClass.Expanded))
            assertEquals(InitializedActivityState(WindowWidthSizeClass.Expanded), awaitItem())
        }
    }
}
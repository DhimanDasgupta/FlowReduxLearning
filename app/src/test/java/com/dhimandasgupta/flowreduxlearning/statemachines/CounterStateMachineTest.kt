package com.dhimandasgupta.flowreduxlearning.statemachines

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CounterStateMachineTest {
    @Test
    fun `test CounterStateMachine Initial State`() = runTest {
        val stateMachine = CounterStateMachine()

        stateMachine.state.test {
            assertEquals(UnInitializedCounterState, awaitItem())
        }
    }

    @Test
    fun `test CounterStateMachine, after MoveToCounterState is fired`() = runTest {
        val stateMachine = CounterStateMachine()

        stateMachine.state.test {
            assertEquals(UnInitializedCounterState, awaitItem())
            stateMachine.dispatch(MoveToCounterState)
            assertEquals(CounterState(0, true), awaitItem())
        }
    }

    @Test
    fun `test CounterStateMachine, after IncrementAction is fired`() = runTest {
        val stateMachine = CounterStateMachine()

        stateMachine.state.test {
            assertEquals(UnInitializedCounterState, awaitItem())
            stateMachine.dispatch(MoveToCounterState)
            assertEquals(CounterState(0, true), awaitItem())
        }
    }

    @Test
    fun `test CounterStateMachine, after DecrementAction is fired`() = runTest {
        val stateMachine = CounterStateMachine()

        stateMachine.state.test {
            assertEquals(UnInitializedCounterState, awaitItem())
            stateMachine.dispatch(MoveToCounterState)
            assertEquals(CounterState(0, true), awaitItem())
        }
    }

    @Test
    fun `test CounterStateMachine, after IncrementAction is fired and Previous state is CounterState`() = runTest {
        val stateMachine = CounterStateMachine()

        stateMachine.state.test {
            assertEquals(UnInitializedCounterState, awaitItem())
            stateMachine.dispatch(MoveToCounterState)
            assertEquals(CounterState(0, true), awaitItem())
            stateMachine.dispatch(IncrementAction)
            assertEquals(CounterState(1, true), awaitItem())
        }
    }

    @Test
    fun `test CounterStateMachine, after DecrementAction is fired and Previous state is CounterState`() = runTest {
        val stateMachine = CounterStateMachine()

        stateMachine.state.test {
            assertEquals(UnInitializedCounterState, awaitItem())
            stateMachine.dispatch(MoveToCounterState)
            assertEquals(CounterState(0, true), awaitItem())
            stateMachine.dispatch(DecrementAction)
            assertEquals(CounterState(-1, true), awaitItem())
        }
    }

    @Test
    fun `test CounterStateMachine, after EnableAction is fired and Previous state is CounterState`() = runTest {
        val stateMachine = CounterStateMachine()

        stateMachine.state.test {
            assertEquals(UnInitializedCounterState, awaitItem())
            stateMachine.dispatch(MoveToCounterState)
            assertEquals(CounterState(0, true), awaitItem())
            stateMachine.dispatch(DisableAction)
            assertEquals(CounterState(0, false), awaitItem())
            stateMachine.dispatch(IncrementAction)
            assertEquals(CounterState(1, false), awaitItem())
            stateMachine.dispatch(EnableAction)
            assertEquals(CounterState(1, true), awaitItem())
        }
    }

    @Test
    fun `test CounterStateMachine, after DisableAction is fired and Previous state is CounterState`() = runTest {
        val stateMachine = CounterStateMachine()

        stateMachine.state.test {
            assertEquals(UnInitializedCounterState, awaitItem())
            stateMachine.dispatch(MoveToCounterState)
            assertEquals(CounterState(0, true), awaitItem())
            stateMachine.dispatch(DisableAction)
            assertEquals(CounterState(0, false), awaitItem())
            stateMachine.dispatch(DecrementAction)
            assertEquals(CounterState(-1, false), awaitItem())
        }
    }

    @Test
    fun `test CounterStateMachine, after ResetAction is fired and Previous state is CounterState`() = runTest {
        val stateMachine = CounterStateMachine()

        stateMachine.state.test {
            assertEquals(UnInitializedCounterState, awaitItem())
            stateMachine.dispatch(MoveToCounterState)
            assertEquals(CounterState(0, true), awaitItem())
            stateMachine.dispatch(DisableAction)
            assertEquals(CounterState(0, false), awaitItem())
            stateMachine.dispatch(DecrementAction)
            assertEquals(CounterState(-1, false), awaitItem())
            stateMachine.dispatch(ResetAction)
            assertEquals(UnInitializedCounterState, awaitItem())
        }
    }
}
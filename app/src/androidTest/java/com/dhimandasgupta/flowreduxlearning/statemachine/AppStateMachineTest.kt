package com.dhimandasgupta.flowreduxlearning.statemachine

import android.content.Context
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

@HiltAndroidTest
class AppStateMachineTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var context: Context

    @Before
    fun init() {
        hiltRule.inject()
    }

    /*@Test
    fun testAppStateMachineInitialState() = runTest {
        val stateMachine = AppStateMachine(context)

        stateMachine.state.test {
            Assert.assertEquals(UnInitializedActivityState, awaitItem())
        }
    }*/
}
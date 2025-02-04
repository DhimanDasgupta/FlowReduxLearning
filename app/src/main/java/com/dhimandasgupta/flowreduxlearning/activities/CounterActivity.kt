package com.dhimandasgupta.flowreduxlearning.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dhimandasgupta.flowreduxlearning.statemachines.CounterAction
import com.dhimandasgupta.flowreduxlearning.statemachines.CounterState
import com.dhimandasgupta.flowreduxlearning.statemachines.DecrementAction
import com.dhimandasgupta.flowreduxlearning.statemachines.DisableAction
import com.dhimandasgupta.flowreduxlearning.statemachines.EnableAction
import com.dhimandasgupta.flowreduxlearning.statemachines.IncrementAction
import com.dhimandasgupta.flowreduxlearning.statemachines.InitializedActivityState
import com.dhimandasgupta.flowreduxlearning.statemachines.MoveToCounterState
import com.dhimandasgupta.flowreduxlearning.statemachines.ResetAction
import com.dhimandasgupta.flowreduxlearning.statemachines.UnInitializedActivityState
import com.dhimandasgupta.flowreduxlearning.statemachines.UnInitializedCounterState
import com.dhimandasgupta.flowreduxlearning.statemachines.WindowSizeChangedAction
import com.dhimandasgupta.flowreduxlearning.ui.theme.FlowReduxLearningTheme
import com.dhimandasgupta.flowreduxlearning.viewmodels.CounterUiState
import com.dhimandasgupta.flowreduxlearning.viewmodels.CounterViewModel
import com.dhimandasgupta.flowreduxlearning.viewmodels.defaultCounterUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CounterActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlowReduxLearningTheme {
                val activityLevelViewModel: CounterViewModel = hiltViewModel()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val counterUiState by activityLevelViewModel.counterState.collectAsState(defaultCounterUiState)
                    CounterUiStateRenderer(
                        state = counterUiState,
                        dispatch = activityLevelViewModel::dispatchCounterAction
                    )

                    val windowSize = calculateWindowSizeClass(activity = this@CounterActivity)
                    LaunchedEffect(key1 = counterUiState.activityState) {
                        activityLevelViewModel.dispatchActivityAction(
                            action = WindowSizeChangedAction(windowSize.widthSizeClass)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CounterUiStateRenderer(
    state: CounterUiState,
    dispatch: (CounterAction) -> Unit
) {
    when (state.activityState) {
        UnInitializedActivityState -> UnInitializedStateRenderer(dispatch = dispatch)
        is InitializedActivityState -> CounterStateStateRenderer(
            state = state,
            dispatch = dispatch
        )
    }
}

@Composable
private fun UnInitializedStateRenderer(
    dispatch: (CounterAction) -> Unit
) {
    Text(
        text = "Uninitialized state",
        modifier = Modifier
            .wrapContentSize()
            .clickable {
                dispatch(MoveToCounterState)
            }
    )
}

@Composable
private fun UnKnowState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Uninitialized state",
            modifier = Modifier
                .wrapContentSize()
        )
    }
}

@Composable
private fun CounterStateStateRenderer(
    state: CounterUiState,
    dispatch: (CounterAction) -> Unit
) {
    when (state.counterState) {
        UnInitializedCounterState -> UnInitializedStateRenderer(dispatch)
        is CounterState -> {
            when (state.activityState) {
                UnInitializedActivityState ->  UnKnowState()
                is InitializedActivityState -> DrawCounter(
                    activityState = state.activityState,
                    state = state.counterState,
                    dispatch = dispatch
                )
            }
        }
    }
}

@Composable
private fun DrawCounter(
    activityState: InitializedActivityState,
    state: CounterState,
    dispatch: (CounterAction) -> Unit
) {
    when (activityState.widthSizeClass) {
        WindowWidthSizeClass.Compact -> DrawCompact(state = state, dispatch = dispatch)
        WindowWidthSizeClass.Medium -> DrawMedium(state = state, dispatch = dispatch)
        else -> DrawExpanded(state = state, dispatch = dispatch)
    }
}

@Composable
private fun DrawCompact(
    state: CounterState,
    dispatch: (CounterAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            StateText(state = state)
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            ButtonLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.CenterVertically),
                showHorizontally = true,
                state = state,
                dispatch = dispatch
            )
        }
    }
}

@Composable
private fun DrawMedium(
    state: CounterState,
    dispatch: (CounterAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            ButtonLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.CenterVertically),
                showHorizontally = true,
                state = state,
                dispatch = dispatch
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            StateText(state = state)
        }
    }
}

@Composable
private fun DrawExpanded(
    state: CounterState,
    dispatch: (CounterAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            StateText(state = state)
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(192.dp)
                .fillMaxHeight()
        ) {
            ButtonLayout(
                modifier = Modifier
                    .fillMaxSize(),
                showHorizontally = false,
                state = state,
                dispatch = dispatch
            )
        }
    }
}

@Composable
private fun ButtonLayout(
    modifier: Modifier = Modifier,
    showHorizontally: Boolean = true,
    state: CounterState,
    dispatch: (CounterAction) -> Unit
) {
    if (showHorizontally) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IncrementButton(state = state, dispatch = dispatch)
            DecrementButton(state = state, dispatch = dispatch)
            ResetButton(state = state, dispatch = dispatch)
        }
    } else {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            IncrementButton(state = state, dispatch = dispatch)
            DecrementButton(state = state, dispatch = dispatch)
            ResetButton(state = state, dispatch = dispatch)
        }
    }
}

@Composable
private fun StateText(
    state: CounterState
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.CenterVertically),
        textAlign = TextAlign.Center,
        text = "$state",
        style = typography.headlineLarge
    )
}

@Composable
private fun IncrementButton(
    state: CounterState,
    dispatch: (CounterAction) -> Unit
) {
    OutlinedButton(
        onClick = {
            dispatch(IncrementAction)
            dispatch(DisableAction)
            dispatch(EnableAction)
        },
        enabled = state.enabled,
        modifier = Modifier.clip(CircleShape)
    ) {
        Text(
            text = "+1",
            modifier = Modifier
                .wrapContentSize()
        )
    }
}

@Composable
private fun DecrementButton(
    state: CounterState,
    dispatch: (CounterAction) -> Unit
) {
    OutlinedButton(
        onClick = { dispatch(DecrementAction) },
        enabled = state.enabled,
        modifier = Modifier.clip(CircleShape)
    ) {
        Text(
            text = "-1",
            modifier = Modifier
                .wrapContentSize()
        )
    }
}

@Composable
private fun ResetButton(
    state: CounterState,
    dispatch: (CounterAction) -> Unit
) {
    OutlinedButton(
        onClick = { dispatch(ResetAction) },
        enabled = state.enabled,
        modifier = Modifier.clip(CircleShape)
    ) {
        Text(
            text = "RESET",
            modifier = Modifier
                .wrapContentSize()
        )
    }
}
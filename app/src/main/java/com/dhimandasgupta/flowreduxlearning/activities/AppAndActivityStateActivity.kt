package com.dhimandasgupta.flowreduxlearning.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dhimandasgupta.flowreduxlearning.statemachines.ActivityState
import com.dhimandasgupta.flowreduxlearning.statemachines.ActivityStateMachine
import com.dhimandasgupta.flowreduxlearning.statemachines.UnInitializedActivityState
import com.dhimandasgupta.flowreduxlearning.statemachines.WindowSizeChangedAction
import com.dhimandasgupta.flowreduxlearning.statemachines.AppState
import com.dhimandasgupta.flowreduxlearning.statemachines.AppStateMachine
import com.dhimandasgupta.flowreduxlearning.statemachines.UnInitializedState
import com.dhimandasgupta.flowreduxlearning.ui.theme.FlowReduxLearningTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AppAndActivityStateActivity : ComponentActivity() {
    @Inject
    lateinit var appStateMachine: AppStateMachine

    @Inject
    lateinit var activityStateMachine: ActivityStateMachine

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Timber.d("Hashcode of $appStateMachine")
        Timber.d("Hashcode of $activityStateMachine")

        setContent {
            val windowSize = calculateWindowSizeClass(activity = this@AppAndActivityStateActivity)

            FlowReduxLearningTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorScheme.background
                ) {
                    val appState = appStateMachine.state.collectAsState(initial = UnInitializedState)
                    val activityState = activityStateMachine.state.collectAsState(initial = UnInitializedActivityState)

                    val combinedState by remember(
                        key1 = activityState
                    ) {
                        derivedStateOf {
                            CombinedState(
                                appState = appState.value,
                                activityState = activityState.value
                            )
                        }
                    }

                    DrawStateToUI(combinedState = combinedState)

                    LaunchedEffect(key1 = activityState) {
                        Timber.d("LaunchedEffect with ${activityState.value}")
                        activityStateMachine.dispatch(
                            WindowSizeChangedAction(
                                widthSizeClass = windowSize.widthSizeClass
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawStateToUI(
    combinedState: CombinedState
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = combinedState.appState.toString(),
            style = typography.headlineLarge,
            modifier = Modifier
                .padding(16.dp)
                .background(
                    color = colorScheme.onTertiary,
                    shape = RoundedCornerShape(
                        size = 16.dp
                    )
                )
                .padding(8.dp)
        )

        Text(
            text = combinedState.activityState.toString(),
            style = typography.headlineLarge,
            color = colorScheme.onTertiary,
            modifier = Modifier
                .padding(16.dp)
                .background(
                    color = colorScheme.tertiary,
                    shape = RoundedCornerShape(
                        size = 16.dp
                    )
                )
                .padding(8.dp)
        )
    }
}


data class CombinedState(
    val appState: AppState,
    val activityState: ActivityState
)


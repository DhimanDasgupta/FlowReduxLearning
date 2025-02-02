package com.dhimandasgupta.flowreduxlearning.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import com.dhimandasgupta.flowreduxlearning.ui.theme.FlowReduxLearningTheme
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.json.JsonNull.content
import kotlin.math.cos
import kotlin.math.sin

class LauncherActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlowReduxLearningTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorScheme.background
                ) {
                    Content()
                    // CircularLayoutScreen()
                }
            }
        }
    }
}

@Composable
private fun CircularLayoutScreen() {
    CircularLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        repeat(10) {
            Box(
                modifier = Modifier
                    .size(75.dp)
                    .clip(CircleShape)
                    .background(colorScheme.onBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (it + 1).toString(),
                    style = typography.headlineSmall,
                    color = colorScheme.background
                )
            }
        }
    }
}

@Composable
private fun CircularLayout(
    modifier: Modifier = Modifier,
    radius: Dp = 150.dp,
    startAngle: Float = -90f,
    content: @Composable () -> Unit
) {
    val radiusPx = with(LocalDensity.current) { radius.toPx() }

    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val placeable = measurables.map { measurable ->
            measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
        }

        val maxChildWidth = placeable.maxOf { it.width }
        val maxChildHeight = placeable.maxOf { it.height }

        val desiredWidth = (2 * radiusPx + maxChildWidth).toInt()
        val desiredHeight = (2 * radiusPx + maxChildHeight).toInt()

        val layoutWidth = constraints.constrainWidth(desiredWidth)
        val layoutHeight = constraints.constrainHeight(desiredHeight)

        val centerX = layoutWidth / 2f
        val centerY = layoutHeight / 2f

        val angleIncrement = 360f / placeable.size

        layout(width = layoutWidth, height = layoutHeight) {
            placeable.forEachIndexed { index, placeable ->
                val angle = startAngle + (index * angleIncrement)
                val angleRadian = Math.toRadians(angle.toDouble())

                val x = centerX + (radiusPx * cos(angleRadian)).toFloat() - placeable.width / 2
                val y = centerY + (radiusPx * sin(angleRadian)).toFloat() - placeable.height / 2

                placeable.placeRelative(
                    x = x.toInt(),
                    y = y.toInt()
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Content() {
    val context = LocalContext.current

    val pagerItems = remember { getPagerItems(context) }

    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { pagerItems.size }
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            ExplanationCard(pagerItem = pagerItems[page])
        }
    }
}

@Composable
private fun ExplanationCard(
    pagerItem: DemoPagerItem
) {
    val scrollState = rememberScrollState()

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.CenterVertically)
            .clickable { pagerItem.onClick() }
            .verticalScroll(scrollState)
    ) {
        Text(
            text = pagerItem.heading,
            style = typography.headlineLarge,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )

        Text(
            text = pagerItem.description,
            style = typography.headlineMedium,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        )
    }
}

@Immutable
private data class DemoPagerItem(
    val heading: String,
    val description: String,
    val onClick: () -> Unit
)

private fun getPagerItems(context: Context) = listOf(
    DemoPagerItem(
        heading = "Lifecycle with FlowRedux",
        description = """
            1. How we can observe a flow in the State Machine.
            (Observe the Network State)
            
            2. How we can update the State Machine when the events
            are Activity dependent.
            (Update the Window Size of the app in the State Machine)
            """.trimIndent(),
        onClick = {
            context.startActivity(
                Intent(context, AppAndActivityStateActivity::class.java)
            )
        }
    ),

    DemoPagerItem(
        heading = "Counter App with FlowRedux",
        description = """
            Example of Counter App

            1. How we can mutate the State in a State Machine
            
            2. Firing multiple Action to mutate different fields of the State Machine
            
            3. How we can send Actions to the State Machine and not update the State
            (Mostly for Logging related stuff)
            """.trimIndent(),
        onClick = {
            context.startActivity(
                Intent(context, CounterActivity::class.java)
            )
        }
    ),

    DemoPagerItem(
        heading = "News Search with FlowRedux",
        description = """
            Example of News Search App

            1. How we can do something when one particular State is Reached.
            
            2. How to mutate the State conditionally.
            
            3. Update the UI for different device configurations.
            """.trimIndent(),
        onClick = {
            context.startActivity(
                Intent(context, NewsSearchActivity::class.java)
            )
        }
    )
).toImmutableList()
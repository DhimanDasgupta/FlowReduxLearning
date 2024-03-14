package com.dhimandasgupta.flowreduxlearning.activities

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dhimandasgupta.flowreduxlearning.common.openBrowserScreen
import com.dhimandasgupta.flowreduxlearning.news.remote.entity.Article
import com.dhimandasgupta.flowreduxlearning.statemachines.InputSearchAction
import com.dhimandasgupta.flowreduxlearning.statemachines.NoSearchState
import com.dhimandasgupta.flowreduxlearning.statemachines.ResetSearchAction
import com.dhimandasgupta.flowreduxlearning.statemachines.SearchFailureState
import com.dhimandasgupta.flowreduxlearning.statemachines.SearchLoadingState
import com.dhimandasgupta.flowreduxlearning.statemachines.SearchSuccessState
import com.dhimandasgupta.flowreduxlearning.statemachines.WindowSizeChangedAction
import com.dhimandasgupta.flowreduxlearning.ui.theme.FlowReduxLearningTheme
import com.dhimandasgupta.flowreduxlearning.viewmodels.NewsSearchUIState
import com.dhimandasgupta.flowreduxlearning.viewmodels.NewsSearchViewModel
import com.dhimandasgupta.flowreduxlearning.viewmodels.defaultNewsSearchUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class NewsSearchActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val windowSize = calculateWindowSizeClass(activity = context as Activity)
            val newsSearchViewModel: NewsSearchViewModel = hiltViewModel()

            FlowReduxLearningTheme {
                val state = newsSearchViewModel.newsSearchUIState.collectAsState(initial = defaultNewsSearchUiState)
                val (textEntered, onTextChanged) = rememberSaveable { mutableStateOf("") }

                RenderNewsScreen(
                    newsSearchUIState = state.value,
                    text = textEntered,
                    onTextChanged = onTextChanged
                )

                LaunchedEffect(key1 = textEntered) {
                    if (textEntered.isNotBlank()) {
                        delay(300)
                        newsSearchViewModel.dispatchAction(InputSearchAction(textEntered))
                    } else {
                        newsSearchViewModel.dispatchAction(ResetSearchAction)
                    }
                }

                LaunchedEffect(key1 = Unit) {
                    newsSearchViewModel.dispatchActivityAction(
                        WindowSizeChangedAction(windowSize.widthSizeClass)
                    )
                }
            }
        }
    }
}

@Composable
private fun RenderNewsScreen(
    newsSearchUIState: NewsSearchUIState,
    text: String,
    onTextChanged: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.background)
    ) {
        TextField(
            value = text,
            onValueChange = onTextChanged,
            label = {
                Text(
                    text = "Start typing here for the news you want"
                )
            },
            placeholder = {
                Text(
                    text = "Your search term")
            },
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )

        when (newsSearchUIState.searchState) {
            NoSearchState -> RenderNoSearchState()
            is SearchLoadingState -> RenderLoadingState(
                loadingState = newsSearchUIState.searchState
            )
            is SearchSuccessState -> RenderSuccessState(
                successState = newsSearchUIState.searchState
            )
            is SearchFailureState -> RenderErrorState(
                failureState = newsSearchUIState.searchState
            )
        }
    }
}

@Composable
private fun RenderNoSearchState() {
    Column(
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.background)
    ) {
        Text(
            text = "Please type to Search some News",
            color = colorScheme.onBackground,
            style = typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun RenderLoadingState(
    loadingState: SearchLoadingState
) {
    val focusManager = LocalFocusManager.current
    focusManager.clearFocus(true)

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.background)
    ) {
        LinearProgressIndicator()
        Text(
            text = "Loading news of ${loadingState.inputSearch}",
            color = colorScheme.onBackground,
            style = typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun RenderSuccessState(
    successState: SearchSuccessState
) {
    // val focusManager = LocalFocusManager.current
    // focusManager.clearFocus(true)

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorScheme.background),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(
            count = successState.articles.size,
            key = { index -> successState.articles[index].hashCode() + index },
            itemContent = { index ->
                ArticleCompact(
                    article = successState.articles[index],
                    onNewsClicked = { url ->
                        openBrowserScreen(
                            context = context,
                            url = url
                        )
                    }
                )
            }
        )
    }
}

@Composable
private fun RenderErrorState(
    failureState: SearchFailureState
) {
    val focusManager = LocalFocusManager.current
    focusManager.clearFocus(true)

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.background)
    ) {
        if (failureState.zeroResult) {
            Text(
                text = "Oops!",
                color = colorScheme.onBackground,
                style = typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
        Text(
            text = failureState.throwable.message ?: "Could not find any news using the ${failureState.inputSearch} as input. \n Please type typing above to find your news.",
            color = colorScheme.onBackground,
            style = typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun ArticleCompact(
    article: Article,
    onNewsClicked: (String?) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 10.dp)
            .clip(RoundedCornerShape(16.dp))
            .wrapContentSize()
            .clickable { onNewsClicked(article.url) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                article.urlToImage?.let { image ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(article.urlToImage)
                            .crossfade(true)
                            .build(),
                        contentDescription = article.content,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(128.dp)
                            .clip(CircleShape)
                    )
                }

                Text(
                    modifier = Modifier
                        .heightIn(min = 16.dp, max = 112.dp)
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                    text = article.title ?: "N/A",
                    style = typography.headlineSmall,
                    color = colorScheme.onBackground,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3
                )
            }

            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentSize(),
                text = article.description ?: "N/A",
                style = typography.labelSmall,
                color = colorScheme.onBackground.copy(alpha = 0.8f)
            )
        }
    }
}
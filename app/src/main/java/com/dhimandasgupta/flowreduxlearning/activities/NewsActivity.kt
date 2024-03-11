package com.dhimandasgupta.flowreduxlearning.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.produceState
import com.dhimandasgupta.flowreduxlearning.news.remote.NewsApiServiceImpl
import com.dhimandasgupta.flowreduxlearning.news.remote.entity.ErrorState
import com.dhimandasgupta.flowreduxlearning.news.remote.entity.LoadingState
import com.dhimandasgupta.flowreduxlearning.news.remote.entity.NewsDataState
import com.dhimandasgupta.flowreduxlearning.news.remote.entity.NewsSource
import com.dhimandasgupta.flowreduxlearning.news.remote.entity.SuccessState
import com.dhimandasgupta.flowreduxlearning.ui.theme.FlowReduxLearningTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class NewsActivity : ComponentActivity() {
    @Inject
    lateinit var newsApiService: NewsApiServiceImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FlowReduxLearningTheme {
                val state = produceState(initialValue = LoadingState(NewsSource("google")) as NewsDataState) {
                    newsApiService
                        .getNews("google")
                        .fold(
                            {
                                value = SuccessState(it)
                            }, {
                                value = ErrorState(it)
                            }
                        )
                }

                when (state.value) {
                    is LoadingState -> {
                        LinearProgressIndicator()
                    }

                    is SuccessState -> {
                        Text(text = state.value.toString())
                    }

                    is ErrorState -> {
                        Timber.d(state.value.toString())
                        Text(text = state.value.toString())
                    }
                }
            }
        }
    }
}
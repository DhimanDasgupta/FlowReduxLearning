package com.dhimandasgupta.flowreduxlearning.di

import android.content.Context
import com.dhimandasgupta.flowreduxlearning.news.remote.NewsApiService
import com.dhimandasgupta.flowreduxlearning.news.remote.NewsApiServiceImpl
import com.dhimandasgupta.flowreduxlearning.statemachines.AppStateMachine
import com.dhimandasgupta.flowreduxlearning.statemachines.NewsSearchStateMachine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.ANDROID
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.http.ContentType
import kotlinx.serialization.json.Json
import okhttp3.internal.immutableListOf
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppStateMachine(@ApplicationContext context: Context) = AppStateMachine(context)

    // Dependencies for News Client
    @Provides
    @Singleton
    fun provideHttpClient() = HttpClient(Android) {
        install(Logging) {
            logger = Logger.ANDROID
            level = LogLevel.ALL
        }
        install(JsonFeature) {
            serializer = KotlinxSerializer(
                Json {
                    ignoreUnknownKeys = true
                    acceptContentTypes = immutableListOf(ContentType.Application.Json)
                    prettyPrint = true
                }
            )
        }
    }

    @Provides
    @Singleton
    fun provideNewsApiService(client: HttpClient): NewsApiService = NewsApiServiceImpl(client)

    @Provides
    @Singleton
    fun provideNewsSearchStateMachine(newsApiService: NewsApiService) = NewsSearchStateMachine(newsApiService)
}
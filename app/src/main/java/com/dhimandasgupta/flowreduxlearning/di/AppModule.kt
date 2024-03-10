package com.dhimandasgupta.flowreduxlearning.di

import android.content.Context
import com.dhimandasgupta.flowreduxlearning.statemachines.AppStateMachine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /*@Provides
    @Singleton
    internal fun provideContext(@ApplicationContext application: Application): Context {
        return application
    }*/

    @Provides
    @Singleton
    fun provideAppStateMachine(@ApplicationContext context: Context) = AppStateMachine(context)
}
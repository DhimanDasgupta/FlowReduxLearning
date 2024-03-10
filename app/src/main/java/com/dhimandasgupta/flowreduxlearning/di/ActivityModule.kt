package com.dhimandasgupta.flowreduxlearning.di

import com.dhimandasgupta.flowreduxlearning.statemachines.ActivityStateMachine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {
    @Provides
    @ActivityScoped
    fun provideActivityStateMachine() = ActivityStateMachine()
}
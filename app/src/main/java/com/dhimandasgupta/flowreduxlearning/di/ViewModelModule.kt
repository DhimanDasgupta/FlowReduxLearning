package com.dhimandasgupta.flowreduxlearning.di

import com.dhimandasgupta.flowreduxlearning.statemachines.ActivityStateMachine
import com.dhimandasgupta.flowreduxlearning.statemachines.CounterStateMachine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    @Provides
    fun provideActivityStateMachine() = ActivityStateMachine()

    @Provides
    @ViewModelScoped
    fun provideCounterStateMachine() = CounterStateMachine()
}
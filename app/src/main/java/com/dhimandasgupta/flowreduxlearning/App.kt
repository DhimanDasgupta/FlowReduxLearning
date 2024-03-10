package com.dhimandasgupta.flowreduxlearning

import android.app.Application
import com.dhimandasgupta.flowreduxlearning.statemachines.AppStateMachine
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var appStateMachine: AppStateMachine

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}
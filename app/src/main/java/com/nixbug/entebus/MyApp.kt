package com.nixbug.entebus

import android.app.Application
import com.nixbug.entebus.di.AppInitializer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application() {

    @Inject
    lateinit var appInitializer: AppInitializer

    override fun onCreate() {
        super.onCreate()

        runBlocking {
            appInitializer.init()
        }
    }
}

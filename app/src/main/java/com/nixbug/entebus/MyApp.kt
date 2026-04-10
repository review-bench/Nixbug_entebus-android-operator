package com.nixbug.entebus

import android.app.Application
import android.util.Log
import coil.ImageLoader
import com.nixbug.entebus.di.AppInitializer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.SupervisorJob
import coil.ImageLoaderFactory

@HiltAndroidApp
class MyApp : Application(),ImageLoaderFactory {

    @Inject
    lateinit var appInitializer: AppInitializer
    @Inject
    lateinit var imageLoader: ImageLoader
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            runCatching {
                appInitializer.init()
            }.onFailure { throwable ->
                Log.e("MyApp", "App initialization failed", throwable)
            }
        }
    }

    override fun newImageLoader(): ImageLoader {
        return imageLoader
    }
}

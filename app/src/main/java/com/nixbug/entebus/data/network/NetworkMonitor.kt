package com.nixbug.entebus.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import com.nixbug.entebus.data.sync.SyncQueueProcessor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMonitor @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val syncQueueProcessor: SyncQueueProcessor
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var isStarted = false

    fun start() {

        if (isStarted) {
            return
        }

        isStarted = true
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        val request = NetworkRequest.Builder().build()

        connectivityManager.registerNetworkCallback(
            request,
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    scope.launch { syncQueueProcessor.processQueue() }
                }
            }
        )
    }
}

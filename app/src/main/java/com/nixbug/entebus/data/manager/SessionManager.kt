package com.nixbug.entebus.data.manager

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {

    private val _sessionExpired = MutableSharedFlow<Unit>(
        replay = 1,
        extraBufferCapacity = 1
    )
    val sessionExpired = _sessionExpired.asSharedFlow()

    fun notifySessionExpired() {
        _sessionExpired.tryEmit(Unit)
    }
}

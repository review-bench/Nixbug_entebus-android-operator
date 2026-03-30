package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.manager.SessionManager
import com.nixbug.entebus.domain.repository.SessionRepository
import javax.inject.Inject
import javax.inject.Singleton

import java.util.concurrent.atomic.AtomicBoolean

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager
) : SessionRepository {

    override val sessionExpired = sessionManager.sessionExpired

    private val isSessionExpiredHandled = AtomicBoolean(false)

    override fun notifySessionExpired() {
        if (isSessionExpiredHandled.compareAndSet(false, true)) {
            sessionManager.notifySessionExpired()
        }
    }

    override fun resetSession() {
        isSessionExpiredHandled.set(false)
    }
}

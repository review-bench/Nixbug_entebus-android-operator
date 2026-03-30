package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.manager.SessionManager
import com.nixbug.entebus.domain.repository.SessionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager
) : SessionRepository {

    override val sessionExpired = sessionManager.sessionExpired

    @Volatile
    private var isSessionExpiredHandled = false

    override fun notifySessionExpired() {
        if (!isSessionExpiredHandled) {
            isSessionExpiredHandled = true
            sessionManager.notifySessionExpired()
        }
    }

    override fun resetSession() {
        isSessionExpiredHandled = false
    }
}

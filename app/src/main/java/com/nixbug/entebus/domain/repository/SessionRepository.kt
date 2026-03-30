package com.nixbug.entebus.domain.repository

import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    val sessionExpired: Flow<Unit>
    fun notifySessionExpired()

    fun resetSession()
}

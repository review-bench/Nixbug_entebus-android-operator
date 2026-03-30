package com.nixbug.entebus.domain.usecase

import com.nixbug.entebus.domain.repository.SessionRepository
import com.nixbug.entebus.domain.repository.TokenRepository
import com.nixbug.entebus.domain.repository.UserRepository
import javax.inject.Inject

class ClearSessionUseCase @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke() {
        tokenRepository.clearToken()
        userRepository.clearUser()
        sessionRepository.notifySessionExpired()
    }
}

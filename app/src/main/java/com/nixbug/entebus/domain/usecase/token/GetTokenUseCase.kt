package com.nixbug.entebus.domain.usecase.token

import com.nixbug.entebus.domain.model.local.AuthToken
import com.nixbug.entebus.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTokenUseCase @Inject constructor(private val repository: TokenRepository) {
    operator fun invoke(): Flow<AuthToken> {
        return repository.getToken()
    }
}

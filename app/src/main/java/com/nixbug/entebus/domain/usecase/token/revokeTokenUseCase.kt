package com.nixbug.entebus.domain.usecase.token

import com.nixbug.entebus.domain.repository.TokenRepository
import com.nixbug.entebus.utils.network.Resource
import javax.inject.Inject

class RevokeTokenUseCase @Inject constructor(private val repository: TokenRepository) {
    suspend operator fun invoke(token: String): Resource<Any> = repository.revokeToken(token)
}
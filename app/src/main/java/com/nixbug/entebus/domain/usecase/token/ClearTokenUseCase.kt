package com.nixbug.entebus.domain.usecase.token

import com.nixbug.entebus.domain.repository.TokenRepository
import javax.inject.Inject

class ClearTokenUseCase @Inject constructor(private val repository: TokenRepository) {
    suspend operator fun invoke() {
        repository.clearToken()
    }
}

package com.nixbug.entebus.domain.usecase.token

import com.nixbug.entebus.domain.model.CreateTokenModel
import com.nixbug.entebus.domain.repository.TokenRepository
import javax.inject.Inject

class SaveTokenUseCase @Inject constructor(private val repository: TokenRepository) {
    suspend operator fun invoke(token: CreateTokenModel) {
        repository.saveToken(token)
    }
}
